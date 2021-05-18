package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.LootUtils;

public class ChanceCondition extends LootCondition {

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
            if (value.endsWith("%"))
                value = value.substring(0, value.length() - 1);
            this.chance = Double.parseDouble(value) / 100;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
