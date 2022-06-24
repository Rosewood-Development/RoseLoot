package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KilledByCondition extends LootCondition {

    private List<EntityType> entityTypes;

    public KilledByCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Optional<Player> lootingPlayer = context.getLootingPlayer();
        if (lootingPlayer.isPresent())
            return this.entityTypes.contains(EntityType.PLAYER);

        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isEmpty())
            return false;

        LivingEntity entity = lootedEntity.get();
        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent event))
            return false;

        Entity damager = LootUtils.propagateKiller(event.getDamager());
        return this.entityTypes.contains(damager.getType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.entityTypes = new ArrayList<>();

        for (String value : values) {
            try {
                if (value.startsWith("#")) {
                    Set<EntityType> tagEntities = LootUtils.getTags(value.substring(1), EntityType.class, "entity_types");
                    if (tagEntities != null) {
                        this.entityTypes.addAll(tagEntities);
                        continue;
                    }
                }

                this.entityTypes.add(EntityType.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.entityTypes.isEmpty();
    }

}
