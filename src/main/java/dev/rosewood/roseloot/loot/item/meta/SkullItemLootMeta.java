package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.nms.SkullUtils;
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
    private String texture;
    private String owner;
    private String hdbId;

    public SkullItemLootMeta(ConfigurationSection section) {
        super(section);

        if (section.isBoolean("copy-looted")) this.copyLooted = section.getBoolean("copy-looted");
        if (section.isBoolean("copy-looter")) this.copyLooter = section.getBoolean("copy-looter");
        if (section.isString("texture")) this.texture = section.getString("texture");
        if (section.isString("owner")) this.owner = section.getString("owner");
        if (section.contains("hdb-id")) this.hdbId = section.getString("hdb-id");
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        Optional<Player> lootedPlayer = context.get(LootContextParams.LOOTED_ENTITY).map(x -> x instanceof Player ? (Player) x : null);
        Optional<Player> lootingPlayer = context.getLootingPlayer();
        if (this.copyLooted && lootedPlayer.isPresent()) {
            itemMeta.setOwningPlayer(lootedPlayer.get());
        } else if (this.copyLooter && lootingPlayer.isPresent()) {
            itemMeta.setOwningPlayer(lootingPlayer.get());
        } else if (this.texture != null) {
            SkullUtils.setSkullTexture(itemMeta, this.texture);
        } else if (this.owner != null) {
            OfflinePlayer offlinePlayer;
            try {
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(this.owner));
            } catch (IllegalArgumentException e) {
                offlinePlayer = Bukkit.getOfflinePlayer(this.owner);
            }
            itemMeta.setOwningPlayer(offlinePlayer);
        } else if (this.hdbId != null && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            SkullUtils.setSkullTexture(itemMeta, new me.arcaniax.hdb.api.HeadDatabaseAPI().getBase64(this.hdbId));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        OfflinePlayer owner = itemMeta.getOwningPlayer();
        if (owner != null) {
            stringBuilder.append("owner: '").append(owner.getUniqueId()).append("'\n");
        } else if (Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            String texture = new me.arcaniax.hdb.api.HeadDatabaseAPI().getBase64(itemStack);
            if (texture != null)
                stringBuilder.append("texture: '").append(texture).append("'\n");
        }
    }

}
