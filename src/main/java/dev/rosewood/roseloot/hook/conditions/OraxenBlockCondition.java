package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.OraxenBlockBreakListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;
import java.util.List;

public class OraxenBlockCondition extends BaseLootCondition {

    private List<String> blockTypes;

    public OraxenBlockCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(OraxenBlockBreakListener.ORAXEN_BLOCK)
                .filter(this.blockTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockTypes = Arrays.asList(values);
        return !this.blockTypes.isEmpty();
    }

}
