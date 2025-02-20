package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Optional;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Steerable;

public class HasSaddleCondition extends BaseLootCondition {

    public HasSaddleCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isEmpty())
            return false;

        LivingEntity entity = lootedEntity.get();
        if (entity instanceof Steerable) {
            return ((Steerable) entity).hasSaddle();
        } else if (entity instanceof AbstractHorse) {
            return ((AbstractHorse) entity).getInventory().getSaddle() != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
