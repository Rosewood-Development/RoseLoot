package dev.rosewood.roseloot.util.nms;

import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class EnchantingUtils {

    private static Object randomSource;
    private static Method method_CraftWorld_getHandle;
    private static Method method_ServerLevel_enabledFeatures;
    private static Method method_ServerLevel_registryAccess;
    private static Method method_EnchantmentManager_enchantItem;
    private static Method method_CraftItemStack_asNMSCopy;
    private static Method method_CraftItemStack_asBukkitCopy;
    static {
        try {
            String version = null;
            String name = Bukkit.getServer().getClass().getPackage().getName();
            if (name.contains("R")) {
                version = name.substring(name.lastIndexOf('.') + 1);
            }

            if (NMSUtil.getVersionNumber() < 17) { // 1.16.5
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.server." + version + ".EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
                Class<?> class_CraftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
                randomSource = new Random();
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", Random.class, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            } else if (NMSUtil.getVersionNumber() < 19) { // 1.17+
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.world.item.enchantment.EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
                Class<?> class_CraftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
                randomSource = new Random();
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", Random.class, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            } else if (NMSUtil.getVersionNumber() < 20 || (NMSUtil.getVersionNumber() == 20 && NMSUtil.getMinorVersionNumber() < 5)) { // 1.19+
                String cbPackage = Bukkit.getServer().getClass().getPackage().getName();
                Class<?> class_RandomSource = Class.forName("net.minecraft.util.RandomSource");
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.world.item.enchantment.EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
                Class<?> class_CraftItemStack = Class.forName(cbPackage + ".inventory.CraftItemStack");
                randomSource = ReflectionUtils.getMethodByName(class_RandomSource, "a").invoke(null);
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", class_RandomSource, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            } else if (NMSUtil.getVersionNumber() < 21) { // 1.20.5+
                String cbPackage = Bukkit.getServer().getClass().getPackage().getName();
                Class<?> class_CraftWorld = Class.forName(cbPackage + ".CraftWorld");
                Class<?> class_ServerLevel = Class.forName("net.minecraft.server.level.ServerLevel");
                Class<?> class_FeatureFlagSet = Class.forName("net.minecraft.world.flag.FeatureFlagSet");
                Class<?> class_RandomSource = Class.forName("net.minecraft.util.RandomSource");
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.world.item.enchantment.EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
                Class<?> class_CraftItemStack = Class.forName(cbPackage + ".inventory.CraftItemStack");
                randomSource = ReflectionUtils.getMethodByName(class_RandomSource, "a").invoke(null);
                method_CraftWorld_getHandle = ReflectionUtils.getMethodByName(class_CraftWorld, "getHandle");
                method_ServerLevel_enabledFeatures = ReflectionUtils.getMethodByName(class_ServerLevel, "enabledFeatures");
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", class_FeatureFlagSet, class_RandomSource, class_ItemStack, int.class, boolean.class);
                method_CraftItemStack_asNMSCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asNMSCopy", ItemStack.class);
                method_CraftItemStack_asBukkitCopy = ReflectionUtils.getMethodByName(class_CraftItemStack, "asBukkitCopy", class_ItemStack);
            } else { // 1.21+
                String cbPackage = Bukkit.getServer().getClass().getPackage().getName();
                Class<?> class_CraftWorld = Class.forName(cbPackage + ".CraftWorld");
                Class<?> class_ServerLevel = Class.forName("net.minecraft.server.level.ServerLevel");
                Class<?> class_RandomSource = Class.forName("net.minecraft.util.RandomSource");
                Class<?> class_EnchantmentManager = Class.forName("net.minecraft.world.item.enchantment.EnchantmentManager");
                Class<?> class_ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
                Class<?> class_CraftItemStack = Class.forName(cbPackage + ".inventory.CraftItemStack");
                Class<?> class_RegistryAccess = Class.forName("net.minecraft.core.RegistryAccess");
                randomSource = ReflectionUtils.getMethodByName(class_RandomSource, "a").invoke(null);
                method_CraftWorld_getHandle = ReflectionUtils.getMethodByName(class_CraftWorld, "getHandle");
                method_ServerLevel_registryAccess = ReflectionUtils.getMethodByName(class_ServerLevel, "H_");
                method_EnchantmentManager_enchantItem = ReflectionUtils.getMethodByName(class_EnchantmentManager, "a", class_RandomSource, class_ItemStack, int.class, class_RegistryAccess, Optional.class);
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
     * @param world The world being enhanted in, used to check enabled features in that world (for enabled enchantments)
     * @return The same ItemStack
     */
    public static ItemStack randomlyEnchant(ItemStack itemStack, int level, boolean treasure, World world) {
        try {
            // Ensures the enchantments get added as the right material
            if (itemStack.getType() == Material.ENCHANTED_BOOK)
                itemStack.setType(Material.BOOK);

            Object nmsItemStack = method_CraftItemStack_asNMSCopy.invoke(null, itemStack);
            if (method_ServerLevel_enabledFeatures != null) {
                Object nmsWorld = method_CraftWorld_getHandle.invoke(world);
                Object enabledFeatures = method_ServerLevel_enabledFeatures.invoke(nmsWorld);
                nmsItemStack = method_EnchantmentManager_enchantItem.invoke(null, enabledFeatures, randomSource, nmsItemStack, level, treasure);
            } else if (method_ServerLevel_registryAccess != null) {
                Object nmsWorld = method_CraftWorld_getHandle.invoke(world);
                Object registryAccess = method_ServerLevel_registryAccess.invoke(nmsWorld);
                nmsItemStack = method_EnchantmentManager_enchantItem.invoke(null, randomSource, nmsItemStack, level, registryAccess, Optional.empty());
            } else {
                nmsItemStack = method_EnchantmentManager_enchantItem.invoke(null, randomSource, nmsItemStack, level, treasure);
            }
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
