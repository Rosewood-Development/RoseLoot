package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.enchantments.Enchantment;

/**
 * enchantment-chance-table:looting,5%,6.25%,8.33%,10%
 * value 1: The enchantment required
 * value 2+: The chance of passing, indexed by the enchantment level
 *
 * An enchantment level greater than the max index will use the max index instead
 */
public class EnchantmentChanceTableCondition extends BaseLootCondition {

    private Enchantment enchantment;
    private List<Double> chances;

    public EnchantmentChanceTableCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        int chanceIndex = Math.min(context.getEnchantmentLevel(this.enchantment), this.chances.size() - 1);
        double chance = this.chances.get(chanceIndex);

        return LootUtils.checkChance(chance);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length < 2)
            return false;

        this.chances = new ArrayList<>();

        try {
            this.enchantment = VersionUtils.getEnchantmentByName(values[0]);

            for (int i = 1; i < values.length; i++) {
                String value = values[i];
                if (value.endsWith("%"))
                    value = value.substring(0, value.length() - 1);
                this.chances.add(Double.parseDouble(value) / 100);
            }

            return !this.chances.isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
