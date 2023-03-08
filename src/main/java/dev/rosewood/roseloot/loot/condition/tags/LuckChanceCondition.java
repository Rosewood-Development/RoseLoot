package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;

public class LuckChanceCondition extends BaseLootCondition {

    private double chance;
    private double luckOffset;

    public LuckChanceCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return LootUtils.checkChance(this.chance + this.luckOffset * context.getLuckLevel());
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 2)
            return false;

        try {
            String chanceValue = values[0];
            if (chanceValue.endsWith("%"))
                chanceValue = chanceValue.substring(0, chanceValue.length() - 1);
            this.chance = Double.parseDouble(chanceValue) / 100;

            String offsetValue = values[1];
            if (offsetValue.endsWith("%"))
                offsetValue = offsetValue.substring(0, offsetValue.length() - 1);
            this.luckOffset = Double.parseDouble(offsetValue) / 100;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
