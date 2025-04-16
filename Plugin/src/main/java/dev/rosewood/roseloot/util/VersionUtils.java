package dev.rosewood.roseloot.util;

import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;

@SuppressWarnings({"deprecation", "removal"})
public class VersionUtils {

    public static final EntityType FIREWORK_ROCKET;
    public static final EntityType ITEM;
    public static final EntityType MOOSHROOM;
    public static final EntityType SNOW_GOLEM;
    public static final Particle DUST;
    public static final Particle PARTICLE_ITEM;
    public static final Particle POOF;
    public static final Particle SMOKE;
    public static final Enchantment FORTUNE;
    public static final Enchantment INFINITY;
    public static final Enchantment LOOTING;
    public static final Enchantment LUCK_OF_THE_SEA;
    public static final Enchantment SWEEPING_EDGE;
    public static final Enchantment UNBREAKING;
    public static final ItemFlag HIDE_ADDITIONAL_TOOLTIP;
    public static final Attribute LUCK;

    static {
        if (NMSUtil.getVersionNumber() > 20 || (NMSUtil.getVersionNumber() == 20 && NMSUtil.getMinorVersionNumber() >= 5)) {
            FIREWORK_ROCKET = EntityType.FIREWORK_ROCKET;
            ITEM = EntityType.ITEM;
            MOOSHROOM = EntityType.MOOSHROOM;
            SNOW_GOLEM = EntityType.SNOW_GOLEM;
            DUST = Particle.DUST;
            PARTICLE_ITEM = Particle.ITEM;
            POOF = Particle.POOF;
            SMOKE = Particle.SMOKE;
            FORTUNE = getEnchantmentByName("fortune");
            INFINITY = getEnchantmentByName("infinity");
            LOOTING = getEnchantmentByName("looting");
            LUCK_OF_THE_SEA = getEnchantmentByName("luck_of_the_sea");
            SWEEPING_EDGE = getEnchantmentByName("sweeping_edge");
            UNBREAKING = getEnchantmentByName("unbreaking");
            HIDE_ADDITIONAL_TOOLTIP = ItemFlag.HIDE_ADDITIONAL_TOOLTIP;
        } else {
            FIREWORK_ROCKET = EntityType.valueOf("FIREWORK");
            ITEM = EntityType.valueOf("DROPPED_ITEM");
            MOOSHROOM = EntityType.valueOf("MUSHROOM_COW");
            SNOW_GOLEM = EntityType.valueOf("SNOWMAN");
            DUST = Particle.valueOf("REDSTONE");
            PARTICLE_ITEM = Particle.valueOf("ITEM_CRACK");
            POOF = Particle.valueOf("EXPLOSION_NORMAL");
            SMOKE = Particle.valueOf("SMOKE_NORMAL");
            FORTUNE = findEnchantmentLegacy("fortune", "loot_bonus_blocks");
            INFINITY = findEnchantmentLegacy("infinity", "arrow_infinite");
            LOOTING = findEnchantmentLegacy("looting", "loot_bonus_mobs");
            LUCK_OF_THE_SEA = findEnchantmentLegacy("luck_of_the_sea", "luck");
            SWEEPING_EDGE = findEnchantmentLegacy("sweeping", "sweeping_edge");
            UNBREAKING = findEnchantmentLegacy("unbreaking", "durability");
            HIDE_ADDITIONAL_TOOLTIP = ItemFlag.valueOf("HIDE_POTION_EFFECTS");
        }

        if (NMSUtil.getVersionNumber() > 21 || NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3) {
            LUCK = Attribute.LUCK;
        } else {
            LUCK = findAttributeLegacy("GENERIC_LUCK");
        }
    }

    private static Enchantment findEnchantmentLegacy(String... names) {
        for (String name : names) {
            Enchantment enchantment = getEnchantmentByName(name);
            if (enchantment != null)
                return enchantment;
        }
        return null;
    }

    private static Method attributeValueOf;
    private static Attribute findAttributeLegacy(String name) {
        try {
            if (attributeValueOf == null)
                attributeValueOf = Attribute.class.getMethod("valueOf", String.class);
            return (Attribute) attributeValueOf.invoke(null, name);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method biomeValueOf;
    public static Biome getBiome(String name) {
        if (NMSUtil.getVersionNumber() > 21 || NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3) {
            return Registry.BIOME.match(name);
        } else {
            try {
                if (biomeValueOf == null)
                    biomeValueOf = Biome.class.getMethod("valueOf", String.class);
                return (Biome) biomeValueOf.invoke(null, name.toUpperCase());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Gets an Enchantment by its registered key
     *
     * @param name The name of the enchantment
     * @return The Enchantment, or null if not found
     */
    public static Enchantment getEnchantmentByName(String name) {
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase());
        if (key == null)
            return null;

        if (NMSUtil.getVersionNumber() > 21 || NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3) {
            return Registry.ENCHANTMENT.get(key);
        } else {
            Enchantment byKey = Enchantment.getByKey(key);
            if (byKey != null)
                return byKey;

            return Arrays.stream(Enchantment.values())
                    .filter(x -> x.getKey().getKey().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }
    }

    public static List<Enchantment> getAllEnchantments() {
        if (NMSUtil.getVersionNumber() > 21 || NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3) {
            return Registry.ENCHANTMENT.stream().toList();
        } else {
            return Arrays.asList(Enchantment.values());
        }
    }

}
