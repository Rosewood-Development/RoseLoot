package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.function.Predicate;

public class BooleanLootCondition extends BaseLootCondition {

    private final Predicate<LootContext> predicate;

    public BooleanLootCondition(String tag, Predicate<LootContext> predicate) {
        super(tag);
        this.predicate = predicate;
    }

    @Override
    public boolean check(LootContext context) {
        return this.predicate.test(context);
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
