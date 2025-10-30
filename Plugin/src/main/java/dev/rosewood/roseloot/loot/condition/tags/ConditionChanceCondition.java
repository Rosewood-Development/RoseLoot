package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConditionChanceCondition extends BaseLootCondition {

    private double falseChance;
    private double trueChance;
    private LootCondition condition;

    public ConditionChanceCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return LootUtils.checkChance(this.condition.check(context) ? this.trueChance : this.falseChance);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length < 3)
            return false;

        String condition = Arrays.stream(values, 1, values.length - 1).collect(Collectors.joining(","));
        this.condition = RoseLoot.getInstance().getManager(LootTableManager.class).parseConditionExpression(condition);
        if (this.condition == null)
            return false;

        try {
            String falseChance = values[0];
            if (falseChance.endsWith("%"))
                falseChance = falseChance.substring(0, falseChance.length() - 1);
            this.falseChance = Double.parseDouble(falseChance) / 100;

            String trueChance = values[values.length - 1];
            if (trueChance.endsWith("%"))
                trueChance = trueChance.substring(0, trueChance.length() - 1);
            this.trueChance = Double.parseDouble(trueChance) / 100;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
