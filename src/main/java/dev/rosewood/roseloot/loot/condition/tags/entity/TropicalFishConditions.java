package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;

public class TropicalFishConditions extends EntityConditions {

    public TropicalFishConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("tropical-fish-body-color", TropicalFishBodyColorCondition.class);
        event.registerLootCondition("tropical-fish-pattern", TropicalFishPatternCondition.class);
        event.registerLootCondition("tropical-fish-pattern-color", TropicalFishPatternColorCondition.class);
    }

    public static class TropicalFishBodyColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public TropicalFishBodyColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, TropicalFish.class)
                    .map(TropicalFish::getBodyColor)
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

    public static class TropicalFishPatternCondition extends LootCondition {

        private List<TropicalFish.Pattern> patterns;

        public TropicalFishPatternCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, TropicalFish.class)
                    .map(TropicalFish::getPattern)
                    .filter(this.patterns::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.patterns = new ArrayList<>();

            for (String value : values) {
                try {
                    TropicalFish.Pattern pattern = TropicalFish.Pattern.valueOf(value.toUpperCase());
                    this.patterns.add(pattern);
                } catch (Exception ignored) { }
            }

            return !this.patterns.isEmpty();
        }

    }

    public static class TropicalFishPatternColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public TropicalFishPatternColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, TropicalFish.class)
                    .map(TropicalFish::getPatternColor)
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
