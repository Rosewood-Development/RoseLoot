package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.function.BiPredicate;

public class StringLootCondition extends LootCondition {

    private final BiPredicate<LootContext, String> predicate;
    private String value;

    public StringLootCondition(String tag, BiPredicate<LootContext, String> predicate) {
        super(tag);
        this.predicate = predicate;
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        return this.predicate.test(context, this.value);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        this.value = values[0];
        return !this.value.isEmpty();
    }

}
