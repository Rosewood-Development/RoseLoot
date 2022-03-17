package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.MushroomCow;

public class MushroomCowConditions extends EntityConditions {

    public MushroomCowConditions(LootConditionRegistrationEvent event) {
        if (NMSUtil.getVersionNumber() >= 14)
            event.registerLootCondition("mooshroom-variant", MooshroomVariantCondition.class);
    }

    public static class MooshroomVariantCondition extends LootCondition {

        private List<MushroomCow.Variant> variants;

        public MooshroomVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, MushroomCow.class)
                    .map(MushroomCow::getVariant)
                    .filter(this.variants::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.variants = new ArrayList<>();

            for (String value : values) {
                try {
                    MushroomCow.Variant variant = MushroomCow.Variant.valueOf(value.toUpperCase());
                    this.variants.add(variant);
                } catch (Exception ignored) { }
            }

            return !this.variants.isEmpty();
        }

    }

}
