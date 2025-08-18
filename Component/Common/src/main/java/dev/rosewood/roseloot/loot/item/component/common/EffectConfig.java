package dev.rosewood.roseloot.loot.item.component.common;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public sealed interface EffectConfig {
    ConsumeEffect toConsumeEffect(LootContext context);

    record TeleportRandomlyConfig(NumberProvider diameter) implements EffectConfig {
        @Override
        public ConsumeEffect toConsumeEffect(LootContext context) {
            return ConsumeEffect.teleportRandomlyEffect(this.diameter.getFloat(context));
        }
    }

    record RemoveEffectsConfig(List<PotionEffectType> effects) implements EffectConfig {
        @Override
        public ConsumeEffect toConsumeEffect(LootContext context) {
            RegistryKeySet<PotionEffectType> registryKeySet = RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, this.effects);
            return ConsumeEffect.removeEffects(registryKeySet);
        }
    }

    record PlaySoundConfig(StringProvider sound) implements EffectConfig {
        @Override
        public ConsumeEffect toConsumeEffect(LootContext context) {
            return ConsumeEffect.playSoundConsumeEffect(Key.key(this.sound.get(context)));
        }
    }

    record ClearAllEffectsConfig() implements EffectConfig {
        @Override
        public ConsumeEffect toConsumeEffect(LootContext context) {
            return ConsumeEffect.clearAllStatusEffects();
        }
    }

    record ApplyEffectsConfig(List<PotionEffect> effects, NumberProvider probability) implements EffectConfig {
        @Override
        public ConsumeEffect toConsumeEffect(LootContext context) {
            return ConsumeEffect.applyStatusEffects(this.effects, this.probability.getFloat(context));
        }
    }
}
