package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;

public class ExplosionCondition extends LootCondition {

    public ExplosionCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getExplosionType() != null;
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
