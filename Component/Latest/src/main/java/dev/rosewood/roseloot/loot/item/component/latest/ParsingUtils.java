package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class ParsingUtils {

    public static List<EffectConfig> parseEffectConfigs(ConfigurationSection section) {
        List<EffectConfig> effects = new ArrayList<>();
        if (section == null)
            return effects;

        Registry<PotionEffectType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
        for (String effectKey : section.getKeys(false)) {
            ConfigurationSection effectSection = section.getConfigurationSection(effectKey);
            if (effectSection == null)
                continue;

            String type = effectSection.getString("type", "");
            switch (type.toLowerCase()) {
                case "teleport-randomly" -> {
                    NumberProvider diameter = NumberProvider.fromSection(effectSection, "diameter", 16.0);
                    effects.add(new EffectConfig.TeleportRandomlyConfig(diameter));
                }
                case "remove-effects" -> {
                    List<PotionEffectType> removeEffects = new ArrayList<>();
                    for (String effectType : effectSection.getStringList("effects")) {
                        try {
                            PotionEffectType potionEffectType = registry.get(Key.key(effectType.toLowerCase()));
                            if (potionEffectType != null)
                                removeEffects.add(potionEffectType);
                        } catch (IllegalArgumentException ignored) { }
                    }

                    if (!removeEffects.isEmpty())
                        effects.add(new EffectConfig.RemoveEffectsConfig(removeEffects));
                }
                case "play-sound" -> {
                    StringProvider soundKey = StringProvider.fromSection(effectSection, "sound", null);
                    if (soundKey != null)
                        effects.add(new EffectConfig.PlaySoundConfig(soundKey));
                }
                case "clear-all-effects" -> effects.add(new EffectConfig.ClearAllEffectsConfig());
                case "apply-effects" -> {
                    List<PotionEffect> potionEffects = parsePotionEffects(effectSection.getConfigurationSection("effects"), registry);
                    if (!potionEffects.isEmpty()) {
                        NumberProvider probability = NumberProvider.fromSection(effectSection, "probability", 1.0);
                        effects.add(new EffectConfig.ApplyEffectsConfig(potionEffects, probability));
                    }
                }
            }
        }
        return effects;
    }

    public static List<PotionEffect> parsePotionEffects(ConfigurationSection section) {
        return parsePotionEffects(section, RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT));
    }

    public static List<PotionEffect> parsePotionEffects(ConfigurationSection section, Registry<PotionEffectType> registry) {
        List<PotionEffect> potionEffects = new ArrayList<>();
        if (section == null)
            return potionEffects;

        for (String key : section.getKeys(false)) {
            ConfigurationSection potionEffectSection = section.getConfigurationSection(key);
            if (potionEffectSection != null) {
                String effectType = potionEffectSection.getString("type");
                if (effectType != null) {
                    PotionEffectType potionEffectType = registry.get(Key.key(effectType.toLowerCase()));
                    if (potionEffectType != null) {
                        int duration = potionEffectSection.getInt("duration", 0);
                        int amplifier = potionEffectSection.getInt("amplifier", 0);
                        boolean ambient = potionEffectSection.getBoolean("ambient", false);
                        boolean particles = potionEffectSection.getBoolean("particles", true);
                        boolean icon = potionEffectSection.getBoolean("icon", true);
                        potionEffects.add(new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon));
                    }
                }
            }
        }
        return potionEffects;
    }

    public static List<ConsumeEffect> translateEffects(List<EffectConfig> effectConfigs, LootContext context) {
        List<ConsumeEffect> effects = new ArrayList<>();
        for (EffectConfig effectConfig : effectConfigs) {
            if (effectConfig instanceof EffectConfig.TeleportRandomlyConfig teleport) {
                effects.add(ConsumeEffect.teleportRandomlyEffect((float) teleport.diameter().getDouble(context)));
            } else if (effectConfig instanceof EffectConfig.RemoveEffectsConfig remove) {
                RegistryKeySet<PotionEffectType> registryKeySet = RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, remove.effects());
                effects.add(ConsumeEffect.removeEffects(registryKeySet));
            } else if (effectConfig instanceof EffectConfig.PlaySoundConfig sound) {
                effects.add(ConsumeEffect.playSoundConsumeEffect(Key.key(sound.sound().get(context))));
            } else if (effectConfig instanceof EffectConfig.ClearAllEffectsConfig) {
                effects.add(ConsumeEffect.clearAllStatusEffects());
            } else if (effectConfig instanceof EffectConfig.ApplyEffectsConfig apply) {
                effects.add(ConsumeEffect.applyStatusEffects(apply.effects(), (float) apply.probability().getDouble(context)));
            }
        }
        return effects;
    }

    public static void applyProperties(List<ConsumeEffect> effects, int indent, StringBuilder stringBuilder) {
        String padding = indent == 0 ? "" : " ".repeat(indent - 1);
        if (!effects.isEmpty()) {
            stringBuilder.append(padding).append("effects:\n");
            int i = 0;
            for (ConsumeEffect effect : effects) {
                stringBuilder.append(padding).append("  ").append(i++).append(":\n");
                if (effect instanceof ConsumeEffect.TeleportRandomly teleport) {
                    stringBuilder.append(padding).append("    type: teleport-randomly\n");
                    stringBuilder.append(padding).append("    diameter: ").append(teleport.diameter()).append('\n');
                } else if (effect instanceof ConsumeEffect.RemoveStatusEffects remove) {
                    stringBuilder.append(padding).append("    type: remove-effects\n");
                    stringBuilder.append(padding).append("    effects:\n");
                    for (var type : remove.removeEffects()) {
                        stringBuilder.append(padding).append("      - ").append(type.key().asMinimalString()).append('\n');
                    }
                } else if (effect instanceof ConsumeEffect.PlaySound playSound) {
                    stringBuilder.append(padding).append("    type: play-sound\n");
                    stringBuilder.append(padding).append("    sound: '").append(playSound.sound().asMinimalString()).append("'\n");
                } else if (effect instanceof ConsumeEffect.ClearAllStatusEffects) {
                    stringBuilder.append(padding).append("    type: clear-all-effects\n");
                } else if (effect instanceof ConsumeEffect.ApplyStatusEffects apply) {
                    stringBuilder.append(padding).append("    type: apply-effects\n");
                    stringBuilder.append(padding).append("    probability: ").append(apply.probability()).append('\n');
                    stringBuilder.append(padding).append("    effects:\n");
                    int j = 0;
                    for (PotionEffect potionEffect : apply.effects()) {
                        stringBuilder.append(padding).append("      ").append(j++).append(":\n");
                        stringBuilder.append(padding).append("        effect: ").append(potionEffect.getType().getKey().asMinimalString()).append('\n');
                        stringBuilder.append(padding).append("        duration: ").append(potionEffect.getDuration()).append('\n');
                        stringBuilder.append(padding).append("        amplifier: ").append(potionEffect.getAmplifier()).append('\n');
                        stringBuilder.append(padding).append("        ambient: ").append(potionEffect.isAmbient()).append('\n');
                        stringBuilder.append(padding).append("        particles: ").append(potionEffect.hasParticles()).append('\n');
                        stringBuilder.append(padding).append("        icon: ").append(potionEffect.hasIcon()).append('\n');
                    }
                }
            }
        }
    }

}
