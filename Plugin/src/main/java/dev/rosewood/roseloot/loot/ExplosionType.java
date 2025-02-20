package dev.rosewood.roseloot.loot;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public enum ExplosionType {

    BLOCK,
    ENTITY,
    CHARGED_ENTITY;

    /**
     * Gets the explosion type from a killed entity
     *
     * @param entity The killed entity
     * @return The explosion type
     */
    public static ExplosionType getDeathExplosionType(LivingEntity entity) {
        EntityDamageEvent event = entity.getLastDamageCause();
        if (event == null)
            return null;

        return switch (event.getCause()) {
            case BLOCK_EXPLOSION -> ExplosionType.BLOCK;
            case ENTITY_EXPLOSION -> {
                if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof Creeper creeper)
                    yield creeper.isPowered() ? ExplosionType.CHARGED_ENTITY : ExplosionType.ENTITY;
                yield ExplosionType.ENTITY;
            }
            default -> null;
        };
    }

}
