package dev.rosewood.roseloot.util;

import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;

@SuppressWarnings({"deprecation", "removal", "UnstableApiUsage"})
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
            FORTUNE = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("fortune"));
            INFINITY = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("infinity"));
            LOOTING = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("looting"));
            LUCK_OF_THE_SEA = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("luck_of_the_sea"));
            SWEEPING_EDGE = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("sweeping_edge"));
            UNBREAKING = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking"));
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
            LUCK = Attribute.valueOf("generic.luck");
        }
    }

    private static Enchantment findEnchantmentLegacy(String... names) {
        for (String name : names) {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(name));
            if (enchantment != null)
                return enchantment;
        }
        return null;
    }

}
