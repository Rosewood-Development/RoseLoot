package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Axolotl;

public class AxolotlConditions extends EntityConditions {

    public AxolotlConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("axolotl-playing-dead", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Axolotl.class).filter(Axolotl::isPlayingDead).isPresent());
        event.registerLootCondition("axolotl-variant", AxolotlVariantCondition.class);
    }

    public static class AxolotlVariantCondition extends LootCondition {

        private List<Axolotl.Variant> variants;

        public AxolotlVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Axolotl.class)
                    .map(Axolotl::getVariant)
                    .filter(this.variants::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.variants = new ArrayList<>();

            for (String value : values) {
                try {
                    Axolotl.Variant variant = Axolotl.Variant.valueOf(value.toUpperCase());
                    this.variants.add(variant);
                } catch (Exception ignored) { }
            }

            return !this.variants.isEmpty();
        }

    }

}
