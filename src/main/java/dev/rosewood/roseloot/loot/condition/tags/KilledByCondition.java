package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KilledByCondition extends LootCondition {

    private List<EntityType> entityTypes;

    public KilledByCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        LivingEntity entity = context.getLootedEntity();
        if (entity == null)
            return false;

        if (context.getLootingPlayer() != null)
            return this.entityTypes.contains(EntityType.PLAYER);

        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return false;

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entity.getLastDamageCause();
        Entity damager = LootUtils.propagateKiller(event.getDamager());
        return this.entityTypes.contains(damager.getType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.entityTypes = new ArrayList<>();

        for (String value : values) {
            try {
                this.entityTypes.add(EntityType.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.entityTypes.isEmpty();
    }

}
