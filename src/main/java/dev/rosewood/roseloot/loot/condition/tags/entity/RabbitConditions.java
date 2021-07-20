package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Rabbit;

public class RabbitConditions extends EntityConditions {

    public RabbitConditions() {
        LootConditions.registerTag("rabbit-type", RabbitTypeCondition.class);
    }

    public static class RabbitTypeCondition extends LootCondition {

        private List<Rabbit.Type> types;

        public RabbitTypeCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Rabbit))
                return false;
            return this.types.contains(((Rabbit) looted).getRabbitType());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Rabbit.Type type = Rabbit.Type.valueOf(value.toUpperCase());
                    this.types.add(type);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

}
