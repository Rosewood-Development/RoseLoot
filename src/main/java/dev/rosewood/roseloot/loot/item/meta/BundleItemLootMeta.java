package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

public class BundleItemLootMeta extends ItemLootMeta {

    private final List<ItemLootItem> lootItems;

    public BundleItemLootMeta(ConfigurationSection section) {
        super(section);

        this.lootItems = new ArrayList<>();

        ConfigurationSection contentsSection = section.getConfigurationSection("contents");
        if (contentsSection != null) {
            for (String key : contentsSection.getKeys(false)) {
                ConfigurationSection itemSection = contentsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    ItemLootItem lootItem = ItemLootItem.fromSection(itemSection);
                    if (lootItem != null)
                        this.lootItems.add(lootItem);
                }
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        BundleMeta itemMeta = (BundleMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        for (ItemLootItem lootItem : this.lootItems)
            for (ItemStack item : lootItem.create(context))
                itemMeta.addItem(item);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
