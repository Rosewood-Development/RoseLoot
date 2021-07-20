package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Steerable;

public class HasSaddleCondition extends LootCondition {

    public HasSaddleCondition(String tag) {
        super(tag);
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        LivingEntity entity = context.getLootedEntity();
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
