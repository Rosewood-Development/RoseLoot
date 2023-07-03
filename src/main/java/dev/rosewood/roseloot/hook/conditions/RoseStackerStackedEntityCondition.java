package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.RoseStackerEntityDeathListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

public class RoseStackerStackedEntityCondition extends BaseLootCondition {

    public RoseStackerStackedEntityCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(RoseStackerEntityDeathListener.STACKED_ENTITY)
                .filter(x -> x.getStackSize() > 1)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
