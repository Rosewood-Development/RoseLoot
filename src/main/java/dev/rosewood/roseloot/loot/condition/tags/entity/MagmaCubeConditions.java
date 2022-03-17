package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
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
            return context.getAs(LootContextParams.LOOTED_ENTITY, MagmaCube.class)
                    .map(MagmaCube::getSize)
                    .filter(this.sizes::contains)
                    .isPresent();
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
