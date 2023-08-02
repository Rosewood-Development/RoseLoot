package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ItemGenerativeLootItem;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

public class BundleItemLootMeta extends ItemLootMeta {

    private final List<ItemGenerativeLootItem> lootItems;

    public BundleItemLootMeta(ConfigurationSection section) {
        super(section);

        this.lootItems = new ArrayList<>();

        ConfigurationSection contentsSection = section.getConfigurationSection("contents");
        if (contentsSection != null) {
            for (String key : contentsSection.getKeys(false)) {
                ConfigurationSection itemSection = contentsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    ItemGenerativeLootItem lootItem = ItemLootItem.fromSection(itemSection); // TODO: Allow any ItemGenerativeLootItem here
                    if (lootItem != null)
                        this.lootItems.add(lootItem);
                }
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        BundleMeta itemMeta = (BundleMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        for (ItemGenerativeLootItem lootItem : this.lootItems)
            for (ItemStack item : lootItem.generate(context))
                itemMeta.addItem(item);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        BundleMeta itemMeta = (BundleMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        List<ItemStack> contents = itemMeta.getItems();
        if (!contents.isEmpty()) {
            stringBuilder.append("contents: \n");
            for (int i = 0; i < contents.size(); i++) {
                ItemStack item = contents.get(i);
                stringBuilder.append("  ").append(i).append(":\n");
                stringBuilder.append("    ").append(ItemLootItem.toSection(item, true).replaceAll(Pattern.quote("\n"), "\n    ").trim()).append('\n');
            }
        }
    }

}
