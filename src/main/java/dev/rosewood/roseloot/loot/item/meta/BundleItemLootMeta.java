package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BundleItemLootMeta extends ItemLootMeta {
    private List<LootItem> lootItems = new ArrayList<>();

    public BundleItemLootMeta(ConfigurationSection section) {
        super(section);
        if (section.isConfigurationSection("content")) {
            ConfigurationSection contentSection = section.getConfigurationSection("content");
            List<String> itemSections = contentSection.getKeys(false).stream().filter((str)->{
                try {
                    Integer.parseInt(str);
                    return true;
                } catch (NumberFormatException e) {

                }
                return false;
            }).sorted((o1, o2) -> Integer.parseInt(o1) < Integer.parseInt(o2) ? -1 : (Integer.parseInt(o1) == Integer.parseInt(o2) ? 0 : 1)).collect(Collectors.toList());
            for (String itemIndex : itemSections) {
                if (contentSection.isConfigurationSection(itemIndex)) {
                    lootItems.add(ItemLootItem.fromSection(contentSection.getConfigurationSection(itemIndex)));
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
