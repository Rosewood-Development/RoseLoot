package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootGenerator;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public abstract class LootItem implements LootGenerator {

    public static LootItem fromSection(LootTableType lootTableType, ConfigurationSection section) {
        LootItemType type = LootItemType.fromString(section.getString("type"));
        if (type == null)
            return null;

        switch (type) {
            case ITEM:
                String itemString = section.getString("item");
                if (itemString == null && lootTableType != LootTableType.BLOCK)
                    return null;

                Material item = itemString == null ? null : Material.matchMaterial(itemString);
                if (item == null && lootTableType != LootTableType.BLOCK)
                    return null;

                int min, max;
                if (section.contains("amount")) {
                    min = max = section.getInt("amount");
                } else {
                    min = section.getInt("min", 1);
                    max = section.getInt("max", 1);
                }

                ConfigurationSection enchantmentBonusSection = section.getConfigurationSection("enchantment-bonus");
                ItemLootItem.EnchantmentBonus enchantmentBonus = null;
                if (enchantmentBonusSection != null) {
                    String enchantmentString = enchantmentBonusSection.getString("enchantment");
                    if (enchantmentString != null) {
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentString.toLowerCase()));
                        int bonusPerLevel = enchantmentBonusSection.getInt("bonus-per-level", -1);
                        if (enchantment != null && bonusPerLevel > 0)
                            enchantmentBonus = new ItemLootItem.EnchantmentBonus(enchantment, bonusPerLevel);
                    }
                }

                ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(item, section);
                return new ItemLootItem(item, min, max, itemLootMeta, enchantmentBonus);

            case EXPERIENCE:
                int minExp, maxExp;
                if (section.contains("amount")) {
                    minExp = maxExp = section.getInt("amount");
                } else {
                    minExp = section.getInt("min", 1);
                    maxExp = section.getInt("max", 1);
                }

                return new ExperienceLootItem(minExp, maxExp);

            case COMMAND:
                if (!section.contains("value"))
                    return null;
                return new CommandLootItem(section.getString("value"));

            default:
                throw new IllegalStateException("Invalid LootItemType specified!");
        }
    }

}
