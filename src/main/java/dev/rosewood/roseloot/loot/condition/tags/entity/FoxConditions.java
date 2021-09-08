package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;

public class FoxConditions extends EntityConditions {

    public FoxConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("fox-type", FoxTypeCondition.class);
        event.registerLootCondition("fox-crouching", context -> context.getLootedEntity() instanceof Fox && ((Fox) context.getLootedEntity()).isCrouching());
    }

    public static class FoxTypeCondition extends LootCondition {

        private List<Fox.Type> types;

        public FoxTypeCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Fox))
                return false;
            return this.types.contains(((Fox) looted).getFoxType());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Fox.Type type = Fox.Type.valueOf(value.toUpperCase());
                    this.types.add(type);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

}
