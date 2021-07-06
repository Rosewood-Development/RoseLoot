package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootGenerator;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.util.EnchantingUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public abstract class LootItem implements LootGenerator {

    @Override
    public boolean check(LootContext context) {
        return true;
    }

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
                if (item == null)
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
                        Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                        int bonusPerLevel = enchantmentBonusSection.getInt("bonus-per-level", 0);
                        boolean addToMax = enchantmentBonusSection.getBoolean("add-to-max", true);
                        if (enchantment != null && bonusPerLevel > 0)
                            enchantmentBonus = new ItemLootItem.EnchantmentBonus(enchantment, bonusPerLevel, addToMax);
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

            case LOOT_TABLE:
                if (!section.contains("value"))
                    return null;
                return new LootTableLootItem(section.getString("value"));

            case EXPLOSION:
                return new ExplosionLootItem(section.getInt("power", 3), section.getBoolean("fire", false), section.getBoolean("break-blocks", true));

            default:
                throw new IllegalStateException("Invalid LootItemType specified!");
        }
    }

}
