package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkItemLootMeta extends ItemLootMeta {

    private final NumberProvider power;
    private List<FireworkEffect> fireworkEffects;

    public FireworkItemLootMeta(ConfigurationSection section) {
        super(section);

        this.power = NumberProvider.fromSection(section, "power", 0);

        ConfigurationSection fireworkEffectSection = section.getConfigurationSection("firework-effects");
        if (fireworkEffectSection != null) {
            this.fireworkEffects = new ArrayList<>();

            for (String key : fireworkEffectSection.getKeys(false)) {
                ConfigurationSection effectSection = fireworkEffectSection.getConfigurationSection(key);
                if (effectSection == null)
                    continue;

                FireworkEffect.Builder builder = FireworkEffect.builder();

                if (effectSection.isString("type")) {
                    String type = effectSection.getString("type");
                    for (FireworkEffect.Type value : FireworkEffect.Type.values()) {
                        if (value.name().equalsIgnoreCase(type)) {
                            builder.with(value);
                            break;
                        }
                    }
                }

                if (effectSection.getBoolean("flicker", false)) builder.withFlicker();
                if (effectSection.getBoolean("trail", false)) builder.withTrail();

                List<String> colors = effectSection.getStringList("colors");
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

                List<String> fadeColors = effectSection.getStringList("fade-colors");
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

                this.fireworkEffects.add(builder.build());
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        if (!(itemStack.getItemMeta() instanceof FireworkMeta itemMeta))
            return itemStack;

        if (this.power != null) itemMeta.setPower(LootUtils.clamp(this.power.getInteger(context), 0, 127));
        if (this.fireworkEffects != null) itemMeta.addEffects(this.fireworkEffects);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof FireworkMeta itemMeta))
            return;

        stringBuilder.append("power: ").append(itemMeta.getPower()).append('\n');

        List<FireworkEffect> effects = itemMeta.getEffects();
        if (!effects.isEmpty()) {
            stringBuilder.append("firework-effects:\n");
            for (int i = 0; i < effects.size(); i++) {
                FireworkEffect effect = effects.get(i);
                stringBuilder.append("  ").append(i).append(":\n");
                stringBuilder.append("    flicker: ").append(effect.hasFlicker()).append('\n');
                stringBuilder.append("    trail: ").append(effect.hasTrail()).append('\n');
                stringBuilder.append("    type: ").append(effect.getType().name().toLowerCase()).append('\n');

                List<Color> colors = effect.getColors();
                if (!colors.isEmpty()) {
                    stringBuilder.append("    colors:\n");
                    for (Color color : colors)
                        stringBuilder.append("      - '").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("'\n");
                }

                List<Color> fadeColors = effect.getFadeColors();
                if (!fadeColors.isEmpty()) {
                    stringBuilder.append("    fade-colors:\n");
                    for (Color color : fadeColors)
                        stringBuilder.append("      - '").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("'\n");
                }
            }
        }
    }

}
