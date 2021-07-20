package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.LivingEntity;

public class CatConditions extends EntityConditions {

    public CatConditions() {
        LootConditions.registerTag("cat-type", CatTypeCondition.class);
        LootConditions.registerTag("cat-collar-color", CatCollarColorCondition.class);
    }

    public static class CatTypeCondition extends LootCondition {

        private List<Cat.Type> types;

        public CatTypeCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Cat))
                return false;
            return this.types.contains(((Cat) looted).getCatType());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Cat.Type type = Cat.Type.valueOf(value.toUpperCase());
                    this.types.add(type);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

    public static class CatCollarColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public CatCollarColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Cat))
                return false;
            return this.colors.contains(((Cat) looted).getCollarColor());
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
