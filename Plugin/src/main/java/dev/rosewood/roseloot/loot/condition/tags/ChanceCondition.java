package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;

public class ChanceCondition extends BaseLootCondition {

    private double chance;

    public ChanceCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return LootUtils.checkChance(this.chance);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length == 0)
            return false;

        try {
            String value = values[0];
            int divisor;
            if (value.endsWith("%")) {
                value = value.substring(0, value.length() - 1);
                divisor = 100;
            } else {
                divisor = 1;
            }
            this.chance = Double.parseDouble(value) / divisor;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
