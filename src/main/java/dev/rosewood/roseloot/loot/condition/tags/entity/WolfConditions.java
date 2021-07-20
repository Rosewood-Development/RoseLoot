package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;

public class WolfConditions extends EntityConditions {

    public WolfConditions() {
        LootConditions.registerTag("wolf-angry", context -> context.getLootedEntity() instanceof Wolf && ((Wolf) context.getLootedEntity()).isAngry());
        LootConditions.registerTag("wolf-color", WolfColorCondition.class);
    }

    public static class WolfColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public WolfColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Wolf))
                return false;
            return this.colors.contains(((Wolf) looted).getCollarColor());
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
