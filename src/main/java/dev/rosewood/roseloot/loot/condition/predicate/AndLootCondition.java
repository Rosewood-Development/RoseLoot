package dev.rosewood.roseloot.loot.condition.predicate;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

public class AndLootCondition implements LootCondition {

    private final LootCondition left, right;

    public AndLootCondition(LootCondition right, LootCondition left) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean check(LootContext context) {
        return this.left.check(context) && this.right.check(context);
    }

}
