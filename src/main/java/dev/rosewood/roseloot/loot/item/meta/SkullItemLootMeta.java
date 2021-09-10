package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.nms.SkullUtils;
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

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.copyLooted && context.getLootedEntity() instanceof Player) {
            Player looted = (Player) context.getLootedEntity();
            itemMeta.setOwningPlayer(looted);
        } else if (this.copyLooter && context.getLooter() instanceof Player) {
            Player looter = (Player) context.getLooter();
            itemMeta.setOwningPlayer(looter);
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

}
