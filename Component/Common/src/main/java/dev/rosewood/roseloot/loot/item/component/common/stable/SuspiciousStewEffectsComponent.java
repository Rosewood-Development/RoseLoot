package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SuspiciousStewEffectsComponent implements LootItemComponent {

    private final List<SuspiciousEffectEntry> effects;

    public SuspiciousStewEffectsComponent(ConfigurationSection section) {
        ConfigurationSection suspiciousStewEffectsSection = section.getConfigurationSection("suspicious-stew-effects");
        if (suspiciousStewEffectsSection != null) {
            this.effects = new ArrayList<>();
            if (suspiciousStewEffectsSection.contains("effects")) {
                ConfigurationSection effectsSection = suspiciousStewEffectsSection.getConfigurationSection("effects");
                if (effectsSection != null) {
                    Registry<PotionEffectType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
                    for (String key : effectsSection.getKeys(false)) {
                        ConfigurationSection effectSection = effectsSection.getConfigurationSection(key);
                        if (effectSection != null) {
                            String effectType = effectSection.getString("type");
                            if (effectType != null) {
                                try {
                                    NamespacedKey namespacedKey = NamespacedKey.minecraft(effectType.toLowerCase());
                                    PotionEffectType potionEffectType = registry.get(namespacedKey);
                                    if (potionEffectType != null) {
                                        int duration = effectSection.getInt("duration", 0);
                                        this.effects.add(SuspiciousEffectEntry.create(potionEffectType, duration));
                                    }
                                } catch (IllegalArgumentException ignored) { }
                            }
                        }
                    }
                }
            }
        } else {
            this.effects = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.effects != null && !this.effects.isEmpty()) {
            itemStack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.suspiciousStewEffects(this.effects));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS))
            return;

        SuspiciousStewEffects suspiciousStewEffects = itemStack.getData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
        if (suspiciousStewEffects.effects().isEmpty())
            return;
            
        stringBuilder.append("suspicious-stew-effects:\n");
        stringBuilder.append("  effects:\n");
        for (int i = 0; i < suspiciousStewEffects.effects().size(); i++) {
            SuspiciousEffectEntry effect = suspiciousStewEffects.effects().get(i);
            stringBuilder.append("    ").append(i).append(":\n");
            stringBuilder.append("      type: ").append(effect.effect().getKey().getKey()).append("\n");
            stringBuilder.append("      duration: ").append(effect.duration()).append("\n");
        }
    }
} 
