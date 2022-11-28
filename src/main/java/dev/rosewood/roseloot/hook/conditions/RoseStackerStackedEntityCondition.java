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
                .map(entity -> RoseStackerAPI.getInstance().getStackedEntity(entity))
                .filter(entity -> entity.getStackSize() > 1)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
