package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.Wolf;

public class WolfConditions extends EntityConditions {

    public WolfConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("wolf-angry", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Wolf.class).filter(Wolf::isAngry).isPresent());
        event.registerLootCondition("wolf-color", WolfColorCondition.class);
    }

    public static class WolfColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public WolfColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Wolf.class)
                    .map(Wolf::getCollarColor)
                    .filter(this.colors::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.colors = new ArrayList<>();

            for (String value : values) {
                try {
                    DyeColor color = DyeColor.valueOf(value.toUpperCase());
                    this.colors.add(color);
                } catch (Exception ignored) { }
            }

            return !this.colors.isEmpty();
        }

    }

}
