package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DeathCauseCondition extends LootCondition {

    private List<DamageCause> damageCauses;

    public DeathCauseCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.LOOTED_ENTITY)
                .map(LivingEntity::getLastDamageCause)
                .map(EntityDamageEvent::getCause)
                .filter(this.damageCauses::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.damageCauses = new ArrayList<>();

        for (String value : values) {
            try {
                this.damageCauses.add(DamageCause.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.damageCauses.isEmpty();
    }

}
