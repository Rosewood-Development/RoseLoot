package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.RoseStackerEntityDeathListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.rosestacker.stack.StackedEntity;
import java.util.Optional;
import org.bukkit.entity.LivingEntity;

public class RoseStackerPrimaryEntityCondition extends BaseLootCondition {

    public RoseStackerPrimaryEntityCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<StackedEntity> stackedEntity = context.get(RoseStackerEntityDeathListener.STACKED_ENTITY);
        Optional<LivingEntity> entity = context.get(LootContextParams.LOOTED_ENTITY);
        if (entity.isEmpty())
            return false;

        if (stackedEntity.isEmpty()) {
            context.getPlaceholders().add("rosestacker_entity_stack_size", 1);
            return true;
        }

        return stackedEntity.get().getEntity() == entity.get();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
