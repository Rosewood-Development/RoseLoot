package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.SkullUtils;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullItemLootMeta extends ItemLootMeta {

    private boolean copyLooted;
    private boolean copyLooter;
    private final StringProvider texture;
    private final StringProvider owner;
    private final StringProvider hdbId;

    public SkullItemLootMeta(ConfigurationSection section) {
        super(section);

        if (section.isBoolean("copy-looted")) this.copyLooted = section.getBoolean("copy-looted");
        if (section.isBoolean("copy-looter")) this.copyLooter = section.getBoolean("copy-looter");
        this.texture = StringProvider.fromSection(section, "texture", null);
        this.owner = StringProvider.fromSection(section, "owner", null);
        this.hdbId = StringProvider.fromSection(section, "hdb-id", null);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        if (!(itemStack.getItemMeta() instanceof SkullMeta itemMeta))
            return itemStack;

        Optional<Player> lootedPlayer = context.get(LootContextParams.LOOTED_ENTITY).map(x -> x instanceof Player ? (Player) x : null);
        Optional<Player> lootingPlayer = context.getLootingPlayer();
        if (this.copyLooted && lootedPlayer.isPresent()) {
            itemMeta.setOwningPlayer(lootedPlayer.get());
        } else if (this.copyLooter && lootingPlayer.isPresent()) {
            itemMeta.setOwningPlayer(lootingPlayer.get());
        } else if (this.texture != null) {
            SkullUtils.setSkullTexture(itemMeta, this.texture.get(context));
        } else if (this.owner != null) {
            OfflinePlayer offlinePlayer;
            try {
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(this.owner.get(context)));
            } catch (IllegalArgumentException e) {
                offlinePlayer = Bukkit.getOfflinePlayer(this.owner.get(context));
            }
            itemMeta.setOwningPlayer(offlinePlayer);
        } else if (this.hdbId != null && Bukkit.getPluginManager().getPlugin("HeadDatabase") != null) {
            SkullUtils.setSkullTexture(itemMeta, new me.arcaniax.hdb.api.HeadDatabaseAPI().getBase64(this.hdbId.get(context)));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta itemMeta))
            return;

        OfflinePlayer owner = itemMeta.getOwningPlayer();
        if (owner != null) {
            stringBuilder.append("owner: '").append(owner.getUniqueId()).append("'\n");
        } else if (Bukkit.getPluginManager().getPlugin("HeadDatabase") != null) {
            String texture = new me.arcaniax.hdb.api.HeadDatabaseAPI().getBase64(itemStack);
            if (texture != null)
                stringBuilder.append("texture: '").append(texture).append("'\n");
        }
    }

}
