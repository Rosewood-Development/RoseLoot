package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Fireworks;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class FireworksComponent implements LootItemComponent {

    private final List<FireworkEffect> effects;
    private final NumberProvider flightDuration;

    public FireworksComponent(ConfigurationSection section) {
        ConfigurationSection fireworksSection = section.getConfigurationSection("fireworks");
        if (fireworksSection != null) {
            this.effects = new ArrayList<>();
            ConfigurationSection effectsSection = fireworksSection.getConfigurationSection("effects");
            if (effectsSection != null) {
                Set<String> keys = effectsSection.getKeys(false);
                for (String key : keys) {
                    ConfigurationSection effectSection = effectsSection.getConfigurationSection(key);
                    if (effectSection == null)
                        continue;

                    FireworkEffect.Type effectType;
                    try {
                        effectType = FireworkEffect.Type.valueOf(effectSection.getString("type", "BALL").toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        effectType = FireworkEffect.Type.BALL;
                    }

                    List<Color> colors = new ArrayList<>();
                    List<String> colorStrings = effectSection.getStringList("colors");
                    for (String colorString : colorStrings) {
                        try {
                            colors.add(Color.fromRGB(java.awt.Color.decode(colorString).getRGB()));
                        } catch (NumberFormatException ignored) {}
                    }

                    List<Color> fadeColors = new ArrayList<>();
                    List<String> fadeColorStrings = effectSection.getStringList("fade-colors");
                    for (String colorString : fadeColorStrings) {
                        try {
                            fadeColors.add(Color.fromRGB(java.awt.Color.decode(colorString).getRGB()));
                        } catch (NumberFormatException ignored) {}
                    }

                    if (!colors.isEmpty()) {
                        this.effects.add(FireworkEffect.builder()
                                .with(effectType)
                                .flicker(effectSection.getBoolean("flicker", false))
                                .trail(effectSection.getBoolean("trail", false))
                                .withColor(colors)
                                .withFade(fadeColors)
                                .build());
                    }
                }
            }

            this.flightDuration = NumberProvider.fromSection(fireworksSection, "flight-duration", null);
        } else {
            this.effects = null;
            this.flightDuration = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.effects != null && this.flightDuration != null) {
            int duration = Math.min(255, Math.max(0, this.flightDuration.getInteger(context)));
            itemStack.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks(this.effects, duration));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.FIREWORKS))
            return;

        Fireworks fireworks = itemStack.getData(DataComponentTypes.FIREWORKS);
        stringBuilder.append("fireworks:\n");
        stringBuilder.append("  flight-duration: ").append(fireworks.flightDuration()).append('\n');
        
        if (!fireworks.effects().isEmpty()) {
            stringBuilder.append("  effects:\n");
            int index = 0;
            for (FireworkEffect effect : fireworks.effects()) {
                stringBuilder.append("    ").append(index++).append(":\n");
                stringBuilder.append("      type: ").append(effect.getType().name()).append('\n');
                stringBuilder.append("      flicker: ").append(effect.hasFlicker()).append('\n');
                stringBuilder.append("      trail: ").append(effect.hasTrail()).append('\n');
                
                if (!effect.getColors().isEmpty()) {
                    stringBuilder.append("      colors:\n");
                    for (Color color : effect.getColors()) {
                        stringBuilder.append("        - '#").append(String.format("%06X", color.asRGB())).append("'\n");
                    }
                }
                
                if (!effect.getFadeColors().isEmpty()) {
                    stringBuilder.append("      fade-colors:\n");
                    for (Color color : effect.getFadeColors()) {
                        stringBuilder.append("        - '#").append(String.format("%06X", color.asRGB())).append("'\n");
                    }
                }
            }
        }
    }

} 
