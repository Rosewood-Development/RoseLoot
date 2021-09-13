package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class FireworkEffectItemLootMeta extends ItemLootMeta {

    private final FireworkEffect fireworkEffect;

    public FireworkEffectItemLootMeta(ConfigurationSection section) {
        super(section);

        FireworkEffect.Builder builder = FireworkEffect.builder();

        if (section.getBoolean("flicker", false)) builder.withFlicker();
        if (section.getBoolean("trail", false)) builder.withTrail();

        if (section.isString("type")) {
            String type = section.getString("type");
            for (FireworkEffect.Type value : FireworkEffect.Type.values()) {
                if (value.name().equalsIgnoreCase(type)) {
                    builder.with(value);
                    break;
                }
            }
        }

        List<String> colors = section.getStringList("colors");
        for (String color : colors) {
            if (color.startsWith("#")) {
                try {
                    java.awt.Color awtColor = java.awt.Color.decode(color);
                    builder.withColor(Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
                } catch (NumberFormatException ignored) { }
            } else {
                Color value = LootUtils.FIREWORK_COLORS.get(color.toUpperCase());
                if (value != null)
                    builder.withColor(value);
            }
        }

        List<String> fadeColors = section.getStringList("fade-colors");
        for (String color : fadeColors) {
            if (color.startsWith("#")) {
                try {
                    builder.withColor(Color.fromRGB(java.awt.Color.decode(color).getRGB()));
                } catch (NumberFormatException ignored) { }
            } else {
                Color value = LootUtils.FIREWORK_COLORS.get(color.toUpperCase());
                if (value != null)
                    builder.withFade(value);
            }
        }

        this.fireworkEffect = builder.build();
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        FireworkEffectMeta itemMeta = (FireworkEffectMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.fireworkEffect != null) itemMeta.setEffect(this.fireworkEffect);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        FireworkEffectMeta itemMeta = (FireworkEffectMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        FireworkEffect effect = itemMeta.getEffect();
        if (effect != null) {
            stringBuilder.append("flicker: ").append(effect.hasFlicker()).append('\n');
            stringBuilder.append("trail: ").append(effect.hasTrail()).append('\n');
            stringBuilder.append("type: ").append(effect.getType().name().toLowerCase()).append('\n');

            List<Color> colors = effect.getColors();
            if (!colors.isEmpty()) {
                stringBuilder.append("colors:\n");
                for (Color color : colors)
                    stringBuilder.append("  - '").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("'\n");
            }

            List<Color> fadeColors = effect.getFadeColors();
            if (!fadeColors.isEmpty()) {
                stringBuilder.append("fade-colors:\n");
                for (Color color : fadeColors)
                    stringBuilder.append("  - '").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("'\n");
            }
        }
    }

}
