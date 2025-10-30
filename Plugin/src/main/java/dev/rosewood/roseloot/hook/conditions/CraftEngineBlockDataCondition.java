package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.CraftEngineBlockBreakListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CraftEngineBlockDataCondition extends BaseLootCondition {

    private List<String> blockData;

    public CraftEngineBlockDataCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<List<String>> dataValuesOptional = context.get(CraftEngineBlockBreakListener.CRAFTENGINE_BLOCK_DATA);
        if (dataValuesOptional.isEmpty())
            return false;

        List<String> dataValues = dataValuesOptional.get();
        return this.blockData.stream().anyMatch(dataValues::contains);
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockData = Arrays.stream(values).map(String::toLowerCase).toList();
        return !this.blockData.isEmpty();
    }

}
