package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.LivingEntity;

public class AxolotlConditions extends EntityConditions {

    public AxolotlConditions() {
        LootConditions.registerTag("axolotl-playing-dead", context -> context.getLootedEntity() instanceof Axolotl && ((Axolotl) context.getLootedEntity()).isPlayingDead());
        LootConditions.registerTag("axolotl-variant", AxolotlVariantCondition.class);
    }

    public static class AxolotlVariantCondition extends LootCondition {

        private List<Axolotl.Variant> variants;

        public AxolotlVariantCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Axolotl))
                return false;
            return this.variants.contains(((Axolotl) looted).getVariant());
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
