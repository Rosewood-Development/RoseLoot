package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Slime;

public class SlimeConditions extends EntityConditions {

    public SlimeConditions() {
        LootConditions.registerTag("slime-size", SlimeSizeCondition.class);
    }

    public static class SlimeSizeCondition extends LootCondition {

        private List<Integer> sizes;

        public SlimeSizeCondition(String tag) {
            super(tag);
        }

        @Override
        protected boolean checkInternal(LootContext context) {
            return context.getLootedEntity() instanceof Slime && this.sizes.contains(((Slime) context.getLootedEntity()).getSize());
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
