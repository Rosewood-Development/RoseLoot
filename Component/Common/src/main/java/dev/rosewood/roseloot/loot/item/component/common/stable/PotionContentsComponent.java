package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionContentsComponent implements LootItemComponent {

    private final PotionType potionType;
    private final Color customColor;
    private final List<PotionEffect> customEffects;
    private final String customName;

    public PotionContentsComponent(ConfigurationSection section) {
        ConfigurationSection potionContentsSection = section.getConfigurationSection("potion-contents");
        if (potionContentsSection != null) {
            // Parse potion type
            String type = potionContentsSection.getString("type");
            this.potionType = type != null ? PotionType.valueOf(type.toUpperCase()) : null;

            // Parse custom color
            Color color = null;
            if (potionContentsSection.contains("color")) {
                try {
                    color = Color.fromRGB(java.awt.Color.decode(potionContentsSection.getString("color")).getRGB());
                } catch (NumberFormatException ignored) { }
            }
            this.customColor = color;

            // Parse custom effects
            this.customEffects = new ArrayList<>();
            if (potionContentsSection.contains("effects")) {
                ConfigurationSection effectsSection = potionContentsSection.getConfigurationSection("effects");
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
                                        int amplifier = effectSection.getInt("amplifier", 0);
                                        boolean ambient = effectSection.getBoolean("ambient", false);
                                        boolean particles = effectSection.getBoolean("particles", true);
                                        boolean icon = effectSection.getBoolean("icon", true);
                                        
                                        this.customEffects.add(new PotionEffect(
                                            potionEffectType,
                                            duration,
                                            amplifier,
                                            ambient,
                                            particles,
                                            icon
                                        ));
                                    }
                                } catch (IllegalArgumentException ignored) { }
                            }
                        }
                    }
                }
            }

            // Parse custom name
            this.customName = potionContentsSection.getString("custom-name");
        } else {
            this.potionType = null;
            this.customColor = null;
            this.customEffects = null;
            this.customName = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.potionType != null || this.customColor != null || !this.customEffects.isEmpty() || this.customName != null) {
            PotionContents.Builder builder = PotionContents.potionContents();
            
            if (this.potionType != null) {
                builder.potion(this.potionType);
            }
            
            if (this.customColor != null) {
                builder.customColor(this.customColor);
            }
            
            if (!this.customEffects.isEmpty()) {
                builder.addCustomEffects(this.customEffects);
            }
            
            if (this.customName != null) {
                builder.customName(this.customName);
            }
            
            itemStack.setData(DataComponentTypes.POTION_CONTENTS, builder.build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.POTION_CONTENTS))
            return;

        PotionContents potionContents = itemStack.getData(DataComponentTypes.POTION_CONTENTS);
        stringBuilder.append("potion-contents:\n");
        
        if (potionContents.potion() != null) {
            stringBuilder.append("  type: ").append(potionContents.potion().name()).append("\n");
        }
        
        if (potionContents.customColor() != null) {
            stringBuilder.append("  color: '#").append(String.format("%06x", potionContents.customColor().asRGB())).append("'\n");;
        }
        
        if (!potionContents.customEffects().isEmpty()) {
            stringBuilder.append("  effects:\n");
            for (int i = 0; i < potionContents.customEffects().size(); i++) {
                PotionEffect effect = potionContents.customEffects().get(i);
                stringBuilder.append("    ").append(i).append(":\n");
                stringBuilder.append("      type: ").append(effect.getType().getKey().getKey()).append("\n");
                stringBuilder.append("      duration: ").append(effect.getDuration()).append("\n");
                stringBuilder.append("      amplifier: ").append(effect.getAmplifier()).append("\n");
                stringBuilder.append("      ambient: ").append(effect.isAmbient()).append("\n");
                stringBuilder.append("      particles: ").append(effect.hasParticles()).append("\n");
                stringBuilder.append("      icon: ").append(effect.hasIcon()).append("\n");
            }
        }
        
        if (potionContents.customName() != null)
            stringBuilder.append("  custom-name: ").append(potionContents.customName()).append("\n");
    }

} 
