package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;

public class SheepConditions extends EntityConditions {

    public SheepConditions() {
        LootConditions.registerTag("sheep-sheared", context -> context.getLootedEntity() instanceof Sheep && !((Sheep) context.getLootedEntity()).isSheared());
        LootConditions.registerTag("sheep-color", SheepColorCondition.class);
    }

    public static class SheepColorCondition extends LootCondition {

        private List<DyeColor> colors;

        public SheepColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Sheep))
                return false;
            return this.colors.contains(((Sheep) looted).getColor());
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
