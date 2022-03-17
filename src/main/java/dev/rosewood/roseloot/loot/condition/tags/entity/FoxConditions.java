package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Fox;

public class FoxConditions extends EntityConditions {

    public FoxConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("fox-type", FoxTypeCondition.class);
        event.registerLootCondition("fox-crouching", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Fox.class).filter(Fox::isCrouching).isPresent());
    }

    public static class FoxTypeCondition extends LootCondition {

        private List<Fox.Type> types;

        public FoxTypeCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Fox.class)
                    .map(Fox::getFoxType)
                    .filter(this.types::contains)
                    .isPresent();
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
