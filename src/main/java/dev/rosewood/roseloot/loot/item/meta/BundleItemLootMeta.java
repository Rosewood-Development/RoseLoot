package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BundleItemLootMeta extends ItemLootMeta {
    private List<LootItem> lootItems = new ArrayList<>();

    public BundleItemLootMeta(ConfigurationSection section) {
        super(section);
        if (section.isConfigurationSection("content")) {
            ConfigurationSection contentSection = section.getConfigurationSection("content");
            Set<String> itemSections = contentSection.getKeys(false);
            List<Integer> indexList = new ArrayList<>();
            for (String itemSection : itemSections) {
                try {
                    indexList.add(Integer.parseInt(itemSection));
                } catch (NumberFormatException e) {

                }
            }
            Collections.sort(indexList);
            for (int itemIndex : indexList) {
                String indexString = String.valueOf(itemIndex);
                if (contentSection.isConfigurationSection(indexString)) {
                    lootItems.add(ItemLootItem.fromSection(contentSection.getConfigurationSection(indexString)));
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

        for (LootItem item : lootItems) {
            Object iStackList = item.create(context);
            if (iStackList instanceof List) {
                for (Object is : (List) iStackList) {
                    if (is instanceof ItemStack) itemMeta.addItem((ItemStack) is);
                }
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
