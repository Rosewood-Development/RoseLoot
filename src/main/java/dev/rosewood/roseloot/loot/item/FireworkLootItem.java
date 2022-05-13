package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class FireworkLootItem implements TriggerableLootItem<FireworkLootItem.FireworkInstance> {

    public static final String DAMAGELESS_METADATA = "damageless";

    private final FireworkInstance fireworkInstance;

    public FireworkLootItem(FireworkInstance fireworkInstance) {
        this.fireworkInstance = fireworkInstance;
    }

    @Override
    public FireworkInstance create(LootContext context) {
        return this.fireworkInstance;
    }

    @Override
    public boolean combineWith(LootItem<?> lootItem) {
        if (!(lootItem instanceof FireworkLootItem))
            return false;

        FireworkLootItem other = (FireworkLootItem) lootItem;
        this.fireworkInstance.combineWith(other.fireworkInstance);
        return true;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        this.create(context).trigger(location);
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

        return new FireworkLootItem(new FireworkInstance(power, dealDamage, fireworkEffects));
    }

    public static class FireworkInstance {

        private final List<NumberProvider> powers;
        private boolean dealDamage;
        private final List<FireworkEffect> effects;

        public FireworkInstance(NumberProvider power, boolean dealDamage, List<FireworkEffect> effects) {
            this.powers = new ArrayList<>(Collections.singletonList(power));
            this.dealDamage = dealDamage;
            this.effects = effects;
        }

        /**
         * Triggers the stored firework state
         *
         * @param location The Location to trigger the firework at
         */
        public void trigger(Location location) {
            World world = location.getWorld();
            if (world != null) {
                int power = Math.min(this.powers.stream().mapToInt(NumberProvider::getInteger).max().orElse(0), 127);
                Firework firework = world.spawn(location, Firework.class, entity -> {
                    FireworkMeta meta = entity.getFireworkMeta();
                    if (power >= 0) meta.setPower(power);
                    meta.addEffects(this.effects);
                    entity.setFireworkMeta(meta);
                    if (!this.dealDamage)
                        entity.setMetadata(DAMAGELESS_METADATA, new FixedMetadataValue(RoseLoot.getInstance(), true));
                });

                if (power < 0)
                    firework.detonate();
            }
        }

        /**
         * Merges another FireworkInstance with this one using the higher values between the two
         *
         * @param other The other FireworkInstance
         */
        public void combineWith(FireworkInstance other) {
            this.powers.addAll(other.powers);
            this.dealDamage |= other.dealDamage;
            this.effects.addAll(other.effects);
        }

    }

}
