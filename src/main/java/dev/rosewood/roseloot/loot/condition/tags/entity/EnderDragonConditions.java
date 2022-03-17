package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.EnderDragon;

public class EnderDragonConditions extends EntityConditions {

    public EnderDragonConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("ender-dragon-phase", EnderDragonPhaseCondition.class);
    }

    public static class EnderDragonPhaseCondition extends LootCondition {

        private List<EnderDragon.Phase> phases;

        public EnderDragonPhaseCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, EnderDragon.class)
                    .map(EnderDragon::getPhase)
                    .filter(this.phases::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.phases = new ArrayList<>();

            for (String value : values) {
                try {
                    EnderDragon.Phase phase = EnderDragon.Phase.valueOf(value.toUpperCase());
                    this.phases.add(phase);
                } catch (Exception ignored) { }
            }

            return !this.phases.isEmpty();
        }

    }

}
