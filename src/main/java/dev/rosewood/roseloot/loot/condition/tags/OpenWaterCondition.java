package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;

public class OpenWaterCondition extends LootCondition {

    public OpenWaterCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getFishHook() != null && context.getFishHook().isInOpenWater();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
