package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionItemLootMeta extends ItemLootMeta {

    private Color color;
    private PotionType potionType;
    private Map<PotionEffectData, Boolean> customEffects;

    public PotionItemLootMeta(ConfigurationSection section) {
        super(section);

        String colorString = section.getString("color");
        if (colorString != null) {
            try {
                java.awt.Color awtColor = java.awt.Color.decode(colorString);
                this.color = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
            } catch (NumberFormatException ignored) { }
        }

        String potionTypeString = section.getString("potion-type");
        for (PotionType value : PotionType.values()) {
            if (value.name().equalsIgnoreCase(potionTypeString)) {
                this.potionType = value;
                break;
            }
        }

        ConfigurationSection customEffectsSection = section.getConfigurationSection("custom-effects");
        if (customEffectsSection != null) {
            this.customEffects = new LinkedHashMap<>();
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
                boolean overwrite = customEffectSection.getBoolean("overwrite", true);

                this.customEffects.put(new PotionEffectData(effect, duration, amplifier, ambient, particles, icon), overwrite);
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        if (!(itemStack.getItemMeta() instanceof PotionMeta itemMeta))
            return itemStack;

        if (this.color != null) itemMeta.setColor(this.color);
        if (this.potionType != null) itemMeta.setBasePotionType(this.potionType);
        if (this.customEffects != null) this.customEffects.forEach((x, y) -> itemMeta.addCustomEffect(x.toPotionEffect(context), y));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof PotionMeta itemMeta))
            return;

        Color color = itemMeta.getColor();
        if (color != null)
            stringBuilder.append("color: '").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("'\n");

        PotionType potionType = itemMeta.getBasePotionType();
        if (potionType != null)
            stringBuilder.append("potion-type: ").append(potionType.name().toLowerCase()).append('\n');

        List<PotionEffect> effects = itemMeta.getCustomEffects();
        if (!effects.isEmpty()) {
            stringBuilder.append("custom-effects:\n");
            for (int i = 0; i < effects.size(); i++) {
                PotionEffect effect = effects.get(i);
                stringBuilder.append("  ").append(i).append(":\n");
                stringBuilder.append("    effect: ").append(effect.getType().getName().toLowerCase()).append('\n');
                stringBuilder.append("    duration: ").append(effect.getDuration()).append('\n');
                stringBuilder.append("    amplifier: ").append(effect.getAmplifier()).append('\n');
                stringBuilder.append("    ambient: ").append(effect.isAmbient()).append('\n');
                stringBuilder.append("    particles: ").append(effect.hasParticles()).append('\n');
                stringBuilder.append("    icon: ").append(effect.hasIcon()).append('\n');
            }
        }
    }

    public static class PotionEffectData {

        private final PotionEffectType potionEffectType;
        private final NumberProvider duration, amplifier;
        private final boolean ambient, particles, icon;

        public PotionEffectData(PotionEffectType potionEffectType, NumberProvider duration, NumberProvider amplifier, boolean ambient, boolean particles, boolean icon) {
            this.potionEffectType = potionEffectType;
            this.duration = duration;
            this.amplifier = amplifier;
            this.ambient = ambient;
            this.particles = particles;
            this.icon = icon;
        }

        public PotionEffect toPotionEffect(LootContext context) {
            return new PotionEffect(this.potionEffectType, this.duration.getInteger(context), this.amplifier.getInteger(context), this.ambient, this.particles, this.icon);
        }

    }

}
