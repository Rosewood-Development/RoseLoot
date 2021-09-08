package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Phantom;

public class PhantomConditions extends EntityConditions {

    public PhantomConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("phantom-size", PhantomSizeCondition.class);
    }

    public static class PhantomSizeCondition extends LootCondition {

        private List<Integer> sizes;

        public PhantomSizeCondition(String tag) {
            super(tag);
        }

        @Override
        protected boolean checkInternal(LootContext context) {
            return context.getLootedEntity() instanceof Phantom && this.sizes.contains(((Phantom) context.getLootedEntity()).getSize());
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
