package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public class SheepConditions extends EntityConditions {

    public SheepConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("sheep-sheared", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Sheep.class).filter(Sheep::isSheared).isPresent());
        event.registerLootCondition("sheep-color", SheepColorCondition.class);
    }

    public static class SheepColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public SheepColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Sheep.class)
                    .map(Sheep::getColor)
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
