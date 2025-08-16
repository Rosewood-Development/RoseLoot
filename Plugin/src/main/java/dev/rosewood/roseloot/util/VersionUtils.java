package dev.rosewood.roseloot.util;

import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;

@SuppressWarnings("deprecation")
public final class VersionUtils {

    private static final boolean HAS_REGISTRY = NMSUtil.getVersionNumber() > 21 || (NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3);
    private static final boolean HAS_REGISTRY_GET_KEY = NMSUtil.isPaper() && HAS_REGISTRY;

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
            FORTUNE = getEnchantment("fortune");
            INFINITY = getEnchantment("infinity");
            LOOTING = getEnchantment("looting");
            LUCK_OF_THE_SEA = getEnchantment("luck_of_the_sea");
            SWEEPING_EDGE = getEnchantment("sweeping_edge");
            UNBREAKING = getEnchantment("unbreaking");
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
            LUCK = getAttribute("GENERIC_LUCK");
        }
    }

    private VersionUtils() {

    }

    private static Method attributeValueOf;
    public static Attribute getAttribute(String id) {
        if (HAS_REGISTRY) {
            NamespacedKey key = NamespacedKey.fromString(id.toLowerCase());
            if (key == null)
                return null;
            return Registry.ATTRIBUTE.get(key);
        }

        try {
            if (attributeValueOf == null)
                attributeValueOf = Attribute.class.getMethod("valueOf", String.class);
            return (Attribute) attributeValueOf.invoke(null, id);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Method biomeValueOf;
    public static Biome getBiome(String id) {
        if (HAS_REGISTRY) {
            NamespacedKey key = NamespacedKey.fromString(id.toLowerCase());
            if (key == null)
                return null;
            return Registry.BIOME.get(key);
        }

        try {
            if (biomeValueOf == null)
                biomeValueOf = Biome.class.getMethod("valueOf", String.class);
            return (Biome) biomeValueOf.invoke(null, id.toUpperCase());
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    public static Enchantment getEnchantment(String id) {
        NamespacedKey key = NamespacedKey.fromString(id.toLowerCase());
        if (key == null)
            return null;

        if (HAS_REGISTRY)
            return Registry.ENCHANTMENT.get(key);

        Enchantment byKey = Enchantment.getByKey(key);
        if (byKey != null)
            return byKey;

        return Arrays.stream(Enchantment.values())
                .filter(x -> x.getKey().getKey().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    private static Method enchantmentValues;
    public static Enchantment[] getEnchantments() {
        if (HAS_REGISTRY)
            return Registry.ENCHANTMENT.stream().toArray(Enchantment[]::new);

        try {
            if (enchantmentValues == null)
                enchantmentValues = Enchantment.class.getMethod("values", Enchantment[].class);
            return (Enchantment[]) enchantmentValues.invoke(null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return new Enchantment[0];
        }
    }

    private static Enchantment findEnchantmentLegacy(String... names) {
        for (String name : names) {
            Enchantment enchantment = getEnchantment(name);
            if (enchantment != null)
                return enchantment;
        }
        return null;
    }

    private static Method patternTypeValueOf;
    public static PatternType getPatternType(String id) {
        if (HAS_REGISTRY) {
            NamespacedKey key = NamespacedKey.fromString(id.toLowerCase());
            if (key == null)
                return null;
            return Registry.BANNER_PATTERN.get(key);
        }

        try {
            if (patternTypeValueOf == null)
                patternTypeValueOf = PatternType.class.getMethod("valueOf", String.class);
            return (PatternType) patternTypeValueOf.invoke(null, id.toUpperCase());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Method patternTypeGetKey;
    public static NamespacedKey getPatternTypeKey(PatternType patternType) {
        if (HAS_REGISTRY_GET_KEY)
            return Registry.BANNER_PATTERN.getKey(patternType);

        try {
            if (patternTypeGetKey == null)
                patternTypeGetKey = PatternType.class.getMethod("getKey", NamespacedKey.class);
            return (NamespacedKey) patternTypeGetKey.invoke(patternType);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
