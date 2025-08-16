package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class FireworkExplosionComponent implements LootItemComponent {

    private final FireworkEffect.Type type;
    private final boolean flicker;
    private final boolean trail;
    private final List<Color> colors;
    private final List<Color> fadeColors;

    public FireworkExplosionComponent(ConfigurationSection section) {
        ConfigurationSection explosionSection = section.getConfigurationSection("firework-explosion");
        if (explosionSection != null) {
            FireworkEffect.Type effectType;
            try {
                effectType = FireworkEffect.Type.valueOf(explosionSection.getString("type", "BALL").toUpperCase());
            } catch (IllegalArgumentException ignored) {
                effectType = FireworkEffect.Type.BALL;
            }
            this.type = effectType;
            this.flicker = explosionSection.getBoolean("flicker", false);
            this.trail = explosionSection.getBoolean("trail", false);
            
            this.colors = new ArrayList<>();
            List<String> colorStrings = explosionSection.getStringList("colors");
            for (String colorString : colorStrings) {
                try {
                    this.colors.add(Color.fromRGB(java.awt.Color.decode(colorString).getRGB()));
                } catch (NumberFormatException ignored) {}
            }
            
            this.fadeColors = new ArrayList<>();
            List<String> fadeColorStrings = explosionSection.getStringList("fade-colors");
            for (String colorString : fadeColorStrings) {
                try {
                    this.fadeColors.add(Color.fromRGB(java.awt.Color.decode(colorString).getRGB()));
                } catch (NumberFormatException ignored) {}
            }
        } else {
            this.type = null;
            this.flicker = false;
            this.trail = false;
            this.colors = null;
            this.fadeColors = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.type != null && this.colors != null && this.fadeColors != null) {
            FireworkEffect effect = FireworkEffect.builder()
                    .with(this.type)
                    .flicker(this.flicker)
                    .trail(this.trail)
                    .withColor(this.colors)
                    .withFade(this.fadeColors)
                    .build();
            
            itemStack.setData(DataComponentTypes.FIREWORK_EXPLOSION, effect);
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.FIREWORK_EXPLOSION))
            return;

        FireworkEffect effect = itemStack.getData(DataComponentTypes.FIREWORK_EXPLOSION);
        stringBuilder.append("firework-explosion:\n");
        stringBuilder.append("  type: ").append(effect.getType().name()).append('\n');
        stringBuilder.append("  flicker: ").append(effect.hasFlicker()).append('\n');
        stringBuilder.append("  trail: ").append(effect.hasTrail()).append('\n');
        
        if (!effect.getColors().isEmpty()) {
            stringBuilder.append("  colors:\n");
            for (Color color : effect.getColors())
                stringBuilder.append("    - '#").append(String.format("%06X", color.asRGB())).append("'\n");
        }
        
        if (!effect.getFadeColors().isEmpty()) {
            stringBuilder.append("  fade-colors:\n");
            for (Color color : effect.getFadeColors())
                stringBuilder.append("    - '#").append(String.format("%06X", color.asRGB())).append("'\n");
        }
    }

} 
