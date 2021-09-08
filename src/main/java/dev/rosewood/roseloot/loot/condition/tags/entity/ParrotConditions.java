package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;

public class ParrotConditions extends EntityConditions {

    public ParrotConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("parrot-variant", ParrotVariantCondition.class);
    }

    public static class ParrotVariantCondition extends LootCondition {

        private List<Parrot.Variant> variants;

        public ParrotVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Parrot))
                return false;
            return this.variants.contains(((Parrot) looted).getVariant());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.variants = new ArrayList<>();

            for (String value : values) {
                try {
                    Parrot.Variant variant = Parrot.Variant.valueOf(value.toUpperCase());
                    this.variants.add(variant);
                } catch (Exception ignored) { }
            }

            return !this.variants.isEmpty();
        }

    }

}
