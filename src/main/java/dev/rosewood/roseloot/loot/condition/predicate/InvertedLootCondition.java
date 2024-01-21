package dev.rosewood.roseloot.loot.condition.predicate;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

public class InvertedLootCondition implements LootCondition {

    private final LootCondition condition;

    public InvertedLootCondition(LootCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean check(LootContext context) {
        return !this.condition.check(context);
    }

}
