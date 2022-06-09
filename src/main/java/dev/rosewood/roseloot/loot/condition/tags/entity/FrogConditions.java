package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Frog;

public class FrogConditions extends EntityConditions {

    public FrogConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("frog-variant", KilledFrogVariantCondition.class);
        event.registerLootCondition("killer-frog-variant", KillerFrogVariantCondition.class);
    }

    public static class KilledFrogVariantCondition extends FrogVariantCondition {

        public KilledFrogVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Frog.class)
                    .map(Frog::getVariant)
                    .filter(this.variants::contains)
                    .isPresent();
        }

    }

    public static class KillerFrogVariantCondition extends FrogVariantCondition {

        public KillerFrogVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTER, Frog.class)
                    .map(Frog::getVariant)
                    .filter(this.variants::contains)
                    .isPresent();
        }

    }

    public static abstract class FrogVariantCondition extends LootCondition {

        protected List<Frog.Variant> variants;

        public FrogVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean parseValues(String[] values) {
            this.variants = new ArrayList<>();

            for (String value : values) {
                try {
                    Frog.Variant variant = Frog.Variant.valueOf(value.toUpperCase());
                    this.variants.add(variant);
                } catch (Exception ignored) { }
            }

            return !this.variants.isEmpty();
        }

    }

}
