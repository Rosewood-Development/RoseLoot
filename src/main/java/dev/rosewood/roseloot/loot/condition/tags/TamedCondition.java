package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

public class TamedCondition extends LootCondition {

    public TamedCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        LivingEntity entity = context.getLootedEntity();
        return entity instanceof Tameable && ((Tameable) entity).isTamed();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
