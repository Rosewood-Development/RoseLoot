package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.NexoBlockBreakListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;
import java.util.List;

public class NexoBlockCondition extends BaseLootCondition {

    private List<String> blockTypes;

    public NexoBlockCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(NexoBlockBreakListener.NEXO_BLOCK)
                .filter(this.blockTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockTypes = Arrays.asList(values);
        return !this.blockTypes.isEmpty();
    }

}
