package dev.rosewood.roseloot.loot.condition.predicate;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

public record OrLootCondition(LootCondition right, LootCondition left) implements LootCondition {

    @Override
    public boolean check(LootContext context) {
        return this.left.check(context) || this.right.check(context);
    }

}
