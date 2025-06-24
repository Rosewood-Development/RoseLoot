package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.CraftEngineBlockBreakListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

import java.util.List;

public class CraftEngineBlockCondition extends BaseLootCondition {

    private List<String> blockTypes;

    public CraftEngineBlockCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(CraftEngineBlockBreakListener.CRAFTENGINE_BLOCK)
                .filter(this.blockTypes::contains)
                .isPresent();
    }

    @Override
    protected boolean parseValues(String[] values) {
        this.blockTypes = List.of(values);
        return !this.blockTypes.isEmpty();
    }

}
