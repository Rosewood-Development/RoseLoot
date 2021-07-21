package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
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

        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return false;

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entity.getLastDamageCause();
        Entity damager = event.getDamager();
        if (damager instanceof TNTPrimed) {
            // Propagate the igniter of the tnt up the stack
            TNTPrimed tntPrimed = (TNTPrimed) damager;
            Entity tntSource = tntPrimed.getSource();
            if (tntSource == null) {
                return this.entityTypes.contains(EntityType.PRIMED_TNT);
            } else {
                damager = tntSource;
            }
        }

        if (damager instanceof Projectile) {
            // Check for the projectile type first, if not fall back to the shooter
            Projectile projectile = (Projectile) damager;
            if (this.entityTypes.contains(projectile.getType())) {
                return true;
            } else if (projectile.getShooter() instanceof Entity) {
                return this.entityTypes.contains(((Entity) projectile.getShooter()).getType());
            }
        }

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
