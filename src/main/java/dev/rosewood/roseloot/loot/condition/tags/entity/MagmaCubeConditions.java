package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.MagmaCube;

public class MagmaCubeConditions extends EntityConditions {

    public MagmaCubeConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("magma-cube-size", MagmaCubeSizeCondition.class);
    }

    public static class MagmaCubeSizeCondition extends LootCondition {

        private List<Integer> sizes;

        public MagmaCubeSizeCondition(String tag) {
            super(tag);
        }

        @Override
        protected boolean checkInternal(LootContext context) {
            return context.getLootedEntity() instanceof MagmaCube && this.sizes.contains(((MagmaCube) context.getLootedEntity()).getSize());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.sizes = new ArrayList<>();

            for (String value : values) {
                try {
                    this.sizes.add(Integer.parseInt(value));
                } catch (Exception ignored) { }
            }

            return !this.sizes.isEmpty();
        }

    }

}
