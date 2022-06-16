package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.item.meta.PotionItemLootMeta;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectLootItem implements TriggerableLootItem<PotionEffectLootItem.PotionEffectInstance> {

    private final PotionEffectInstance potionEffectInstance;

    public PotionEffectLootItem(PotionEffectInstance potionEffectInstance) {
        this.potionEffectInstance = potionEffectInstance;
    }

    @Override
    public PotionEffectInstance create(LootContext context) {
        return this.potionEffectInstance;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        context.getAs(LootContextParams.LOOTER, LivingEntity.class).ifPresent(x -> this.create(context).trigger(x));
    }

    public static PotionEffectLootItem fromSection(ConfigurationSection section) {
        ConfigurationSection customEffectsSection = section.getConfigurationSection("custom-effects");
        List<PotionItemLootMeta.PotionEffectData> customEffects = new ArrayList<>();
        if (customEffectsSection != null) {
            for (String key : customEffectsSection.getKeys(false)) {
                ConfigurationSection customEffectSection = customEffectsSection.getConfigurationSection(key);
                if (customEffectSection == null)
                    continue;

                String effectString = customEffectSection.getString("effect");
                if (effectString == null)
                    continue;

                PotionEffectType effect = PotionEffectType.getByName(effectString);
                if (effect == null)
                    continue;

                NumberProvider duration = NumberProvider.fromSection(customEffectSection, "duration", 200);
                NumberProvider amplifier = NumberProvider.fromSection(customEffectSection, "amplifier", 0);
                boolean ambient = customEffectSection.getBoolean("ambient", false);
                boolean particles = customEffectSection.getBoolean("particles", true);
                boolean icon = customEffectSection.getBoolean("icon", true);

                customEffects.add(new PotionItemLootMeta.PotionEffectData(effect, duration, amplifier, ambient, particles, icon));
            }
        }
        return new PotionEffectLootItem(new PotionEffectInstance(customEffects));
    }

    public record PotionEffectInstance(List<PotionItemLootMeta.PotionEffectData> effects) {

        /**
         * Triggers the stored explosion state
         *
         * @param target The LivingEntity to apply the potion effects to
         */
        public void trigger(LivingEntity target) {
            this.effects.forEach(x -> target.addPotionEffect(x.toPotionEffect()));
        }

    }

}
