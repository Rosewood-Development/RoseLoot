package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VersionUtils;
import org.bukkit.enchantments.Enchantment;

/**
 * enchantment-chance:10%,looting,3%
 * value 1: Base percentage
 * value 2: The enchantment
 * value 3: Extra chance to add for each level of the enchantment
 * value 4: The maximum number of levels to count towards increasing the percentage
 */
public class EnchantmentChanceCondition extends BaseLootCondition {

    private double chance;
    private Enchantment enchantment;
    private double chancePerLevel;
    private int maxCountedLevels;

    public EnchantmentChanceCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return LootUtils.checkChance(this.chance + this.chancePerLevel * Math.min(context.getEnchantmentLevel(this.enchantment), this.maxCountedLevels));
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 3 && values.length != 4)
            return false;

        try {
            String value1 = values[0];
            if (value1.endsWith("%"))
                value1 = value1.substring(0, value1.length() - 1);

            String value2 = values[2];
            if (value2.endsWith("%"))
                value2 = value2.substring(0, value2.length() - 1);

            this.chance = Double.parseDouble(value1) / 100;
            this.enchantment = VersionUtils.getEnchantmentByName(values[1]);
            this.chancePerLevel = Double.parseDouble(value2) / 100;
            this.maxCountedLevels = values.length == 4 ? Integer.parseInt(values[3]) : Integer.MAX_VALUE;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
