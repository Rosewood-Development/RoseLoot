package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.PufferFish;

public class PufferfishConditions extends EntityConditions {

    public PufferfishConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("pufferfish-puff-state", PufferfishPuffStateCondition.class);
    }

    public static class PufferfishPuffStateCondition extends LootCondition {

        private List<Integer> puffStates;

        public PufferfishPuffStateCondition(String tag) {
            super(tag);
        }

        @Override
        protected boolean checkInternal(LootContext context) {
            return context.getLootedEntity() instanceof PufferFish && this.puffStates.contains(((PufferFish) context.getLootedEntity()).getPuffState());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.puffStates = new ArrayList<>();

            for (String value : values) {
                try {
                    this.puffStates.add(Integer.parseInt(value));
                } catch (Exception ignored) { }
            }

            return !this.puffStates.isEmpty();
        }

    }

}
