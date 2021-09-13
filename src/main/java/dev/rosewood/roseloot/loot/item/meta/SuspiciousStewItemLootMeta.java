package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuspiciousStewItemLootMeta extends ItemLootMeta {

    private Map<PotionItemLootMeta.PotionEffectData, Boolean> customEffects;

    public SuspiciousStewItemLootMeta(ConfigurationSection section) {
        super(section);

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

                this.customEffects.put(new PotionItemLootMeta.PotionEffectData(effect, duration, amplifier, ambient, particles, icon), overwrite);
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        SuspiciousStewMeta itemMeta = (SuspiciousStewMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.customEffects != null) this.customEffects.forEach((x, y) -> itemMeta.addCustomEffect(x.toPotionEffect(), y));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        SuspiciousStewMeta itemMeta = (SuspiciousStewMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

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

}
