package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class StringLootCondition extends LootCondition {

    private final BiPredicate<LootContext, List<String>> predicate;
    private List<String> values;

    public StringLootCondition(String tag, BiPredicate<LootContext, List<String>> predicate) {
        super(tag);
        this.predicate = predicate;
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        return this.predicate.test(context, this.values);
    }

    @Override
    public boolean parseValues(String[] values) {
        this.values = Arrays.stream(values).map(String::toLowerCase).collect(Collectors.toList());
        return !this.values.isEmpty();
    }

}
