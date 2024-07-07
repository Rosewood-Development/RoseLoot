package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class FireworkLootItem implements TriggerableLootItem {

    public static final String DAMAGELESS_METADATA = "damageless";

    private final NumberProvider power;
    private final List<FireworkEffect> effects;
    private final boolean dealDamage;

    public FireworkLootItem(NumberProvider power, List<FireworkEffect> effects, boolean dealDamage) {
        this.power = power;
        this.effects = new ArrayList<>(effects);
        this.dealDamage = dealDamage;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        int power = LootUtils.clamp(this.power.getInteger(context), 0, 127);
        Firework firework = EntitySpawnUtil.spawn(location, Firework.class, entity -> {
            FireworkMeta meta = entity.getFireworkMeta();
            meta.setPower(power);
            meta.addEffects(this.effects);
            entity.setFireworkMeta(meta);
            if (!this.dealDamage)
                entity.setMetadata(DAMAGELESS_METADATA, new FixedMetadataValue(RoseLoot.getInstance(), true));
        });

        if (power < 0)
            firework.detonate();
    }

    public static FireworkLootItem fromSection(ConfigurationSection section) {
        NumberProvider power = NumberProvider.fromSection(section, "power", 1);
        boolean dealDamage = section.getBoolean("deal-damage", false);
        List<FireworkEffect> fireworkEffects = new ArrayList<>();

        ConfigurationSection fireworkEffectSection = section.getConfigurationSection("firework-effects");
        if (fireworkEffectSection != null) {
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
                            java.awt.Color awtColor = java.awt.Color.decode(color);
                            builder.withFade(Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
                        } catch (NumberFormatException ignored) { }
                    } else {
                        Color value = LootUtils.FIREWORK_COLORS.get(color.toUpperCase());
                        if (value != null)
                            builder.withFade(value);
                    }
                }

                fireworkEffects.add(builder.build());
            }
        }

        return new FireworkLootItem(power, fireworkEffects, dealDamage);
    }

}
