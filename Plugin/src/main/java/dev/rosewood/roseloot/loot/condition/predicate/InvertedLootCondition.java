package dev.rosewood.roseloot.loot.condition.predicate;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

public record InvertedLootCondition(LootCondition condition) implements LootCondition {

    @Override
    public boolean check(LootContext context) {
        return !this.condition.check(context);
    }

}
