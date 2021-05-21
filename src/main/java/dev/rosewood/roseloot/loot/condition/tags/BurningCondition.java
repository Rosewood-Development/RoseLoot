package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;

public class BurningCondition extends LootCondition {

    public BurningCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getLootedEntity() != null && context.getLootedEntity().getFireTicks() > 0;
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
