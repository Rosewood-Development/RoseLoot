package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuspiciousStewItemLootMeta extends ItemLootMeta {

    private Map<PotionEffect, Boolean> customEffects;

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

                int duration = customEffectSection.getInt("duration", 200);
                int amplifier = customEffectSection.getInt("amplifier", 0);
                boolean ambient = customEffectSection.getBoolean("ambient", false);
                boolean particles = customEffectSection.getBoolean("particles", true);
                boolean icon = customEffectSection.getBoolean("icon", true);
                boolean overwrite = customEffectSection.getBoolean("overwrite", true);

                this.customEffects.put(new PotionEffect(effect, duration, amplifier, ambient, particles, icon), overwrite);
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        SuspiciousStewMeta itemMeta = (SuspiciousStewMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.customEffects != null) this.customEffects.forEach(itemMeta::addCustomEffect);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
