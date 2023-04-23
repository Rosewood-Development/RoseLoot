package dev.rosewood.roseloot.util.nms;

import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class EnchantingUtils {

    private static Object randomSource;
    private static Method method_EnchantmentManager_enchantItem;
    private static Method method_CraftItemStack_asNMSCopy;
    private static Method method_CraftItemStack_asBukkitCopy;
    static {
        try {
            if (NMSUtil.getVersionNumber() < 17) { // 1.16.5
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.server." + NMSUtil.getVersion() + ".EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.server." + NMSUtil.getVersion() + ".ItemStack");
                Class<?> class_CraftItemStack = Class.forName("org.bukkit.craftbukkit." + NMSUtil.getVersion() + ".inventory.CraftItemStack");
                randomSource = new Random();
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", Random.class, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            } else if (NMSUtil.getVersionNumber() < 19) { // 1.17+
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.world.item.enchantment.EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
                Class<?> class_CraftItemStack = Class.forName("org.bukkit.craftbukkit." + NMSUtil.getVersion() + ".inventory.CraftItemStack");
                randomSource = new Random();
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", Random.class, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            } else { // 1.19+
                Class<?> class_RandomSource = Class.forName("net.minecraft.util.RandomSource");
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.world.item.enchantment.EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
                Class<?> class_CraftItemStack = Class.forName("org.bukkit.craftbukkit." + NMSUtil.getVersion() + ".inventory.CraftItemStack");
                randomSource = ReflectionUtils.getMethodByName(class_RandomSource, "a").invoke(null);
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", class_RandomSource, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private EnchantingUtils() {

    }

    /**
     * Randomly enchants an item using vanilla logic
     *
     * @param itemStack The ItemStack to enchant
     * @param level The level of the enchant (equivalent to enchanting table levels)
     * @param treasure Whether or not treasure enchantments will be included (ex. mending)
     * @return The same ItemStack
     */
    public static ItemStack randomlyEnchant(ItemStack itemStack, int level, boolean treasure) {
        try {
            Object nmsItemStack = method_CraftItemStack_asNMSCopy.invoke(null, itemStack);
            nmsItemStack = method_EnchantmentManager_enchantItem.invoke(null, randomSource, nmsItemStack, level, treasure);
            return (ItemStack) method_CraftItemStack_asBukkitCopy.invoke(null, nmsItemStack);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return itemStack;
        }
    }

    /**
     * Gets an Enchantment by its registered key
     *
     * @param name The name of the enchantment
     * @return The Enchantment, or null if not found
     */
    public static Enchantment getEnchantmentByName(String name) {
        Enchantment byKey = Enchantment.getByKey(NamespacedKey.fromString(name.toLowerCase()));
        if (byKey != null)
            return byKey;

        return Arrays.stream(Enchantment.values())
                .filter(x -> x.getKey().getKey().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
