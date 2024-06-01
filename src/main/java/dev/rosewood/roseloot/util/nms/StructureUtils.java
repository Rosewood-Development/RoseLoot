package dev.rosewood.roseloot.util.nms;

import com.google.common.collect.BiMap;
import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.World;

public final class StructureUtils {

    private static BiMap<?, ?> structures;
    private static Method method_CraftWorld_getHandle;
    private static Constructor<?> constructor_BlockPosition;
    private static Method method_WorldServer_getStructureManager;
    private static Method method_StructureManager_a;
    private static Method method_StructureStart_e;

    static {
        try {
            String version = null;
            String name = Bukkit.getServer().getClass().getPackage().getName();
            if (name.contains("R")) {
                version = name.substring(name.lastIndexOf('.') + 1);
            }

            Class<?> class_StructureGenerator;
            Class<?> class_CraftWorld;
            Class<?> class_BlockPosition;
            Class<?> class_WorldServer;
            Class<?> class_StructureManager;
            Class<?> class_StructureStart;

            if (NMSUtil.getVersionNumber() >= 17) {
                String cbPackage = Bukkit.getServer().getClass().getPackage().getName();
                class_StructureGenerator = Class.forName("net.minecraft.world.level.levelgen.feature.StructureGenerator");
                class_CraftWorld = Class.forName(cbPackage + ".CraftWorld");
                class_BlockPosition = Class.forName("net.minecraft.core.BlockPosition");
                class_WorldServer = Class.forName("net.minecraft.server.level.WorldServer");
                class_StructureManager = Class.forName("net.minecraft.world.level.StructureManager");
                class_StructureStart = Class.forName("net.minecraft.world.level.levelgen.structure.StructureStart");
            } else {
                class_StructureGenerator = Class.forName("net.minecraft.server." + version + ".StructureGenerator");
                class_CraftWorld = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
                class_BlockPosition = Class.forName("net.minecraft.server." + version + ".BlockPosition");
                class_WorldServer = Class.forName("net.minecraft.server." + version + ".WorldServer");
                class_StructureManager = Class.forName("net.minecraft.server." + version + ".StructureManager");
                class_StructureStart = Class.forName("net.minecraft.server." + version + ".StructureStart");
            }

            Field field_StructureGenerator_a = class_StructureGenerator.getDeclaredField("a");
            structures = (BiMap<?, ?>) field_StructureGenerator_a.get(null);
            method_CraftWorld_getHandle = class_CraftWorld.getDeclaredMethod("getHandle");
            constructor_BlockPosition = class_BlockPosition.getConstructor(int.class, int.class, int.class);
            method_WorldServer_getStructureManager = class_WorldServer.getDeclaredMethod("getStructureManager");
            method_StructureManager_a = class_StructureManager.getDeclaredMethod("a", class_BlockPosition, boolean.class, class_StructureGenerator);
            method_StructureStart_e = class_StructureStart.getDeclaredMethod("e");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the given Location is within a certain type of structure
     *
     * @param location The Location to check at
     * @param structureType The StructureType to check for
     * @return true if the Location is within the StructureType, false otherwise
     */
    public static boolean isWithinStructure(Location location, StructureType structureType) {
        World world = location.getWorld();
        if (world == null)
            return false;

        Location structureLocation = world.locateNearestStructure(location, structureType, 1, false);
        if (structureLocation == null)
            return false;

        try {
            Object structureGenerator = structures.get(structureType.getName());
            Object nmsWorld = method_CraftWorld_getHandle.invoke(world);
            Object blockPosition = constructor_BlockPosition.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object structureManager = method_WorldServer_getStructureManager.invoke(nmsWorld);
            Object structureStart = method_StructureManager_a.invoke(structureManager, blockPosition, true, structureGenerator);
            return (boolean) method_StructureStart_e.invoke(structureStart);
        } catch (Exception e) {
            return false;
        }
    }

}
