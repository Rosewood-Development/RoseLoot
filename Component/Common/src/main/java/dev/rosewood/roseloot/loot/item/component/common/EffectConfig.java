package dev.rosewood.roseloot.loot.item.component.common;

import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public sealed interface EffectConfig {
    record TeleportRandomlyConfig(NumberProvider diameter) implements EffectConfig {}
    record RemoveEffectsConfig(List<PotionEffectType> effects) implements EffectConfig {}
    record PlaySoundConfig(StringProvider sound) implements EffectConfig {}
    record ClearAllEffectsConfig() implements EffectConfig {}
    record ApplyEffectsConfig(List<PotionEffect> effects, NumberProvider probability) implements EffectConfig {}
}
