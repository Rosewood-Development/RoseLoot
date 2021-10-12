package dev.rosewood.roseloot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantingUtils {

    private static final Random RANDOM = new Random();

    private EnchantingUtils() {

    }

    /**
     * Gets an Enchantment by its vanilla name
     *
     * @param name The name of the enchantment
     * @return The Enchantment, or null if not found
     */
    public static Enchantment getEnchantmentByName(String name) {
        return Enchantment.getByKey(NamespacedKey.fromString(name.toLowerCase()));
    }

    /**
     * Randomly enchants an item using vanilla logic
     *
     * @param itemStack The ItemStack to enchant
     * @param level The level of the enchant (equivalent to enchanting table levels)
     * @param treasure Whether or not treasure enchantments will be included (ex. mending)
     * @param uncapped A for-fun value, removes the max enchantment cost cap to allow for crazy enchantments
     * @return The same ItemStack
     */
    public static ItemStack randomlyEnchant(ItemStack itemStack, int level, boolean treasure, boolean uncapped) {
        if (itemStack.getType() == Material.BOOK)
            itemStack.setType(Material.ENCHANTED_BOOK);

        List<EnchantmentInstance> enchantments = selectEnchantment(itemStack, level, treasure, uncapped);
        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            if (meta == null)
                return itemStack;

            for (EnchantmentInstance enchantment : enchantments)
                meta.addStoredEnchant(enchantment.getEnchantment().asSpigot(), enchantment.getLevel(), true);

            itemStack.setItemMeta(meta);
        } else {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null)
                return itemStack;

            for (EnchantmentInstance enchantment : enchantments)
                meta.addEnchant(enchantment.getEnchantment().asSpigot(), enchantment.getLevel(), true);

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    private static List<EnchantmentInstance> selectEnchantment(ItemStack itemStack, int level, boolean treasure, boolean uncapped) {
        List<EnchantmentInstance> enchantments = new ArrayList<>();
        int enchantmentValue = getEnchantmentValue(itemStack.getType());
        if (enchantmentValue <= 0)
            return enchantments;

        level += 1 + RANDOM.nextInt(enchantmentValue / 4 + 1) + RANDOM.nextInt(enchantmentValue / 4 + 1);
        float offset = (RANDOM.nextFloat() + RANDOM.nextFloat() - 1.0f) * 0.15f;
        level = LootUtils.clamp(Math.round((float) level + (float) level * offset), 1, Integer.MAX_VALUE);
        RandomCollection<EnchantmentInstance> possibleEnchantments = new RandomCollection<>();
        for (EnchantmentInstance instance : getAvailableEnchantmentResults(itemStack, level, treasure, uncapped))
            possibleEnchantments.add(instance.getEnchantment().getWeight(), instance);

        if (!possibleEnchantments.isEmpty()) {
            enchantments.add(possibleEnchantments.removeNext());
            while (RANDOM.nextInt(50) <= level && !possibleEnchantments.isEmpty()) {
                filterCompatibleEnchantments(possibleEnchantments, enchantments.get(enchantments.size() - 1));
                if (possibleEnchantments.isEmpty())
                    break;

                enchantments.add(possibleEnchantments.removeNext());
                level /= 2;
            }
        }

        return enchantments;
    }

    private static List<EnchantmentInstance> getAvailableEnchantmentResults(ItemStack itemStack, int level, boolean treasure, boolean uncapped) {
        List<EnchantmentInstance> enchantments = new ArrayList<>();
        boolean book = itemStack.getType() == Material.ENCHANTED_BOOK;
        outer: for (EnchantmentInfo enchantment : EnchantmentInfo.getAvailableValues()) {
            if ((enchantment.asSpigot().isTreasure() && !treasure) || !enchantment.isDiscoverable() || (!enchantment.asSpigot().canEnchantItem(itemStack) && !book))
                continue;

            int maxLevel = uncapped ? (enchantment.asSpigot().getMaxLevel() == 1 ? 1 : 10) : enchantment.asSpigot().getMaxLevel();
            for (int i = maxLevel; i > enchantment.asSpigot().getStartLevel() - 1; --i) {
                if (level < enchantment.getMinCost(i) || (level > enchantment.getMaxCost(i) && !uncapped))
                    continue;

                enchantments.add(new EnchantmentInstance(enchantment, i));
                continue outer;
            }
        }

        return enchantments;
    }

    private static void filterCompatibleEnchantments(RandomCollection<EnchantmentInstance> enchantments, EnchantmentInstance enchantment) {
        enchantments.removeIf(x -> x.getEnchantment().asSpigot().conflictsWith(enchantment.getEnchantment().asSpigot()));
    }

    private static int getEnchantmentValue(Material material) {
        switch (material) {
            case WOODEN_SHOVEL:
            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_HOE:
            case WOODEN_SWORD:
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
            case NETHERITE_SHOVEL:
            case NETHERITE_PICKAXE:
            case NETHERITE_AXE:
            case NETHERITE_HOE:
            case NETHERITE_SWORD:
                return 15;
            case STONE_SHOVEL:
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_HOE:
            case STONE_SWORD:
                return 5;
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return 12;
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case TURTLE_HELMET:
                return 9;
            case IRON_SHOVEL:
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_HOE:
            case IRON_SWORD:
                return 14;
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
                return 25;
            case GOLDEN_SHOVEL:
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_HOE:
            case GOLDEN_SWORD:
                return 22;
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case DIAMOND_SHOVEL:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_SWORD:
                return 10;
            case ENCHANTED_BOOK:
            case FISHING_ROD:
            case TRIDENT:
            case BOW:
            case CROSSBOW:
                return 1;
            default:
                return 0;
        }
    }

    // spigot please expose this thanks
    public enum EnchantmentInfo {
        PROTECTION(Rarity.COMMON) {
            public int getMinCost(int level) { return 1 + (level - 1) * 11; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 11; }
        },
        FIRE_PROTECTION(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 10 + (level - 1) * 8; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 8; }
        },
        FEATHER_FALLING(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 5 + (level - 1) * 6; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 6; }
        },
        BLAST_PROTECTION(Rarity.RARE) {
            public int getMinCost(int level) { return 5 + (level - 1) * 8; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 8; }
        },
        PROJECTILE_PROTECTION(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 3 + (level - 1) * 6; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 6; }
        },
        RESPIRATION(Rarity.RARE) {
            public int getMinCost(int level) { return 10 * level; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 30; }
        },
        AQUA_AFFINITY(Rarity.RARE) {
            public int getMinCost(int level) { return 1; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 40; }
        },
        THORNS(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return 10 + 20 * (level - 1); }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        DEPTH_STRIDER(Rarity.RARE) {
            public int getMinCost(int level) { return level * 10; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 15; }
        },
        FROST_WALKER(Rarity.RARE) {
            public int getMinCost(int level) { return level * 10; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 15; }
        },
        BINDING_CURSE(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return 25; }
            public int getMaxCost(int level) { return 50; }
        },
        SOUL_SPEED(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return level * 10; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 15; }
            public boolean isDiscoverable() { return false; }
        },
        SHARPNESS(Rarity.COMMON) {
            public int getMinCost(int level) { return 1 + (level - 1) * 11; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 20; }
        },
        SMITE(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 5 + (level - 1) * 8; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 20; }
        },
        BANE_OF_ARTHROPODS(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 5 + (level - 1) * 8; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 20; }
        },
        KNOCKBACK(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 5 + 20 * (level - 1); }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        FIRE_ASPECT(Rarity.RARE) {
            public int getMinCost(int level) { return 10 + 20 * (level - 1); }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        LOOTING(Rarity.RARE) {
            public int getMinCost(int level) { return 15 + (level - 1) * 9; }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        SWEEPING(Rarity.RARE) {
            public int getMinCost(int level) { return 5 + (level - 1) * 9; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 15; }
        },
        EFFICIENCY(Rarity.COMMON) {
            public int getMinCost(int level) { return 1 + 10 * (level - 1); }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        SILK_TOUCH(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return 15; }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        UNBREAKING(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 5 + (level - 1) * 8; }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        FORTUNE(Rarity.RARE) {
            public int getMinCost(int level) { return 15 + (level - 1) * 9; }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        POWER(Rarity.COMMON) {
            public int getMinCost(int level) { return 1 + (level - 1) * 10; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 15; }
        },
        PUNCH(Rarity.RARE) {
            public int getMinCost(int level) { return 12 + (level - 1) * 20; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 25; }
        },
        FLAME(Rarity.RARE) {
            public int getMinCost(int level) { return 20; }
            public int getMaxCost(int level) { return 50; }
        },
        INFINITY(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return 20; }
            public int getMaxCost(int level) { return 50; }
        },
        LUCK_OF_THE_SEA(Rarity.RARE) {
            public int getMinCost(int level) { return 15 + (level - 1) * 9; }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        LURE(Rarity.RARE) {
            public int getMinCost(int level) { return 15 + (level - 1) * 9; }
            public int getMaxCost(int level) { return super.getMinCost(level) + 50; }
        },
        LOYALTY(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 5 + level * 7; }
            public int getMaxCost(int level) { return 50; }
        },
        IMPALING(Rarity.RARE) {
            public int getMinCost(int level) { return 1 + (level - 1) * 8; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 20; }
        },
        RIPTIDE(Rarity.RARE) {
            public int getMinCost(int level) { return 10 + level * 7; }
            public int getMaxCost(int level) { return 50; }
        },
        CHANNELING(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return 25; }
            public int getMaxCost(int level) { return 50; }
        },
        MULTISHOT(Rarity.RARE) {
            public int getMinCost(int level) { return 20; }
            public int getMaxCost(int level) { return 50; }
        },
        QUICK_CHARGE(Rarity.UNCOMMON) {
            public int getMinCost(int level) { return 12 + (level - 1) * 20; }
            public int getMaxCost(int level) { return 50; }
        },
        PIERCING(Rarity.COMMON) {
            public int getMinCost(int level) { return 1 + (level - 1) * 10; }
            public int getMaxCost(int level) { return 50; }
        },
        MENDING(Rarity.RARE) {
            public int getMinCost(int level) { return level * 25; }
            public int getMaxCost(int level) { return this.getMinCost(level) + 50; }
        },
        VANISHING_CURSE(Rarity.VERY_RARE) {
            public int getMinCost(int level) { return 25; }
            public int getMaxCost(int level) { return 50; }
        };

        static final Map<Enchantment, EnchantmentInfo> BY_ENCHANTMENT;
        static {
            BY_ENCHANTMENT = new HashMap<>();
            for (EnchantmentInfo value : values())
                BY_ENCHANTMENT.put(value.asSpigot(), value);
        }

        private final Enchantment enchantment;
        private final Rarity rarity;

        EnchantmentInfo(Rarity rarity) {
            this.enchantment = getEnchantmentByName(this.name());
            this.rarity = rarity;
        }

        public Enchantment asSpigot() {
            return this.enchantment;
        }

        public int getWeight() {
            return this.rarity.getWeight();
        }

        public int getMinCost(int level) {
            return 1 + level * 10;
        }

        public int getMaxCost(int level) {
            return this.getMinCost(level) + 5;
        }

        public boolean isDiscoverable() {
            return true;
        }

        public static EnchantmentInfo fromEnchantment(Enchantment enchantment) {
            return BY_ENCHANTMENT.get(enchantment);
        }

        public static List<EnchantmentInfo> getAvailableValues() {
            return Arrays.stream(values()).filter(x -> x.asSpigot() != null).collect(Collectors.toList());
        }

    }

    public enum Rarity {

        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return this.weight;
        }

    }

    private static class EnchantmentInstance {

        private final EnchantmentInfo enchantment;
        private final int level;

        public EnchantmentInstance(EnchantmentInfo enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        public EnchantmentInfo getEnchantment() {
            return this.enchantment;
        }

        public int getLevel() {
            return this.level;
        }

    }

}
