package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.function.Predicate;

public class BooleanLootCondition extends LootCondition {

    private final Predicate<LootContext> predicate;

    public BooleanLootCondition(String tag, Predicate<LootContext> predicate) {
        super(tag);
        this.predicate = predicate;
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        return this.predicate.test(context);
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
