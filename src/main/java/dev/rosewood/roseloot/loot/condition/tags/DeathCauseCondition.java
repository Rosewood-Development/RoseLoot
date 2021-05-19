package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
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
    public boolean check(LootContext context) {
        LivingEntity entity = context.getLootedEntity();
        if (entity == null)
            return false;

        EntityDamageEvent event = entity.getLastDamageCause();
        if (event == null)
            return false;

        return this.damageCauses.contains(event.getCause());
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
