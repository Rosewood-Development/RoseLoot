package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.rosestacker.api.RoseStackerAPI;

public class RoseStackerStackedEntityCondition extends LootCondition {

    public RoseStackerStackedEntityCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.LOOTED_ENTITY)
                .filter(entity -> RoseStackerAPI.getInstance().isEntityStacked(entity))
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
