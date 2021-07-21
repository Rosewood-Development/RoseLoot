package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;

public class EnderDragonConditions extends EntityConditions {

    public EnderDragonConditions() {
        LootConditions.registerTag("ender-dragon-phase", EnderDragonPhaseCondition.class);
    }

    public static class EnderDragonPhaseCondition extends LootCondition {

        private List<EnderDragon.Phase> phases;

        public EnderDragonPhaseCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof EnderDragon))
                return false;
            return this.phases.contains(((EnderDragon) looted).getPhase());
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
