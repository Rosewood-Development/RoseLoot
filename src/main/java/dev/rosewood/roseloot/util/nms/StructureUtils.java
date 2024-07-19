package dev.rosewood.roseloot.util.nms;

import com.google.common.collect.BiMap;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.generator.structure.Structure;

public final class StructureUtils {

    private static AtomicReference<StructureHandler> structureHandler = null;

    /**
     * Checks if the given Location is within a certain type of structure
     *
     * @param location The Location to check at
     * @param structureType The StructureType to check for
     * @return true if the Location is within the StructureType, false otherwise
     */
    public static boolean isWithinStructure(Location location, StructureType structureType) {
        StructureHandler structureHandler = getStructureHandler();
        if (structureHandler == null)
            return false;

        try {
            return structureHandler.isWithinStructure(location, structureType.getKey());
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    /**
     * Checks if the given Location is within a certain type of structure
     *
     * @param location The Location to check at
     * @param structure The Structure to check for
     * @return true if the Location is within the StructureType, false otherwise
     */
    public static boolean isWithinStructure(Location location, Structure structure) {
        StructureHandler structureHandler = getStructureHandler();
        if (structureHandler == null)
            return false;

        try {
            return structureHandler.isWithinStructure(location, Registry.STRUCTURE.getKeyOrThrow(structure));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static StructureHandler getStructureHandler() {
        if (structureHandler == null) {
            try {
                StructureHandler handler;
                if (NMSUtil.getVersionNumber() >= 19) {
                    handler = new StructureHandler_v1_19();
                } else if (NMSUtil.getVersionNumber() >= 17) {
                    handler = new StructureHandler_v1_17();
                } else {
                    handler = new StructureHandler_v1_16();
                }
                structureHandler = new AtomicReference<>(handler);
            } catch (ReflectiveOperationException e) {
                RoseLoot.getInstance().getLogger().severe("Failed to load structure location handler, the `feature` condition will not work.");
                e.printStackTrace();
                structureHandler = new AtomicReference<>(null);
            }
        }
        return structureHandler.get();
    }

    private interface StructureHandler {
        boolean isWithinStructure(Location location, NamespacedKey structureKey) throws ReflectiveOperationException;
    }

    private static class StructureHandler_v1_19 implements StructureHandler {

        private final Method method_CraftWorld_getHandle;
        private final Method method_ServerLevel_structureManager;
        private final Method method_ServerLevel_registryAccess;
        private final Constructor<?> constructor_BlockPos;
        private final Method method_Registry_getOrThrow;
        private final Class<?> class_Structure;
        private final Method method_StructureManager_getStructureWithPieceAt;
        private final Method method_RegistryAccess_registryOrThrow;
        private final Field field_Registries_STRUCTURE;
        private final Method method_ResourceLocation_parse;
        private final Method method_ResourceKey_create;
        private final Field field_StructureStart_INVALID_START;

        public StructureHandler_v1_19() throws ReflectiveOperationException {
            String name = Bukkit.getServer().getClass().getPackage().getName();

            Class<?> class_CraftWorld = Class.forName(name + ".CraftWorld");
            Class<?> class_ServerLevel = Class.forName("net.minecraft.server.level.WorldServer");
            this.method_CraftWorld_getHandle = class_CraftWorld.getDeclaredMethod("getHandle");
            this.method_ServerLevel_structureManager = class_ServerLevel.getDeclaredMethod("a");
            Class<?> class_BlockPos = Class.forName("net.minecraft.core.BlockPosition");
            this.constructor_BlockPos = class_BlockPos.getConstructor(int.class, int.class, int.class);
            this.method_ServerLevel_registryAccess = class_ServerLevel.getMethod("H_");
            Class<?> class_Registry = Class.forName("net.minecraft.core.IRegistry");
            Class<?> class_ResourceKey = Class.forName("net.minecraft.resources.ResourceKey");
            this.method_Registry_getOrThrow = class_Registry.getDeclaredMethod("f", class_ResourceKey);
            Class<?> class_StructureManager = Class.forName("net.minecraft.world.level.StructureManager");
            this.class_Structure = Class.forName("net.minecraft.world.level.levelgen.structure.Structure");
            this.method_StructureManager_getStructureWithPieceAt = class_StructureManager.getDeclaredMethod("b", class_BlockPos, this.class_Structure);
            Class<?> class_RegistryAccess = Class.forName("net.minecraft.core.IRegistryCustom");
            this.method_RegistryAccess_registryOrThrow = class_RegistryAccess.getDeclaredMethod("d", class_ResourceKey);
            Class<?> class_Registries = Class.forName("net.minecraft.core.registries.Registries");
            this.field_Registries_STRUCTURE = class_Registries.getField("aR");
            Class<?> class_ResourceLocation = Class.forName("net.minecraft.resources.MinecraftKey");
            this.method_ResourceLocation_parse = class_ResourceLocation.getDeclaredMethod("a", String.class);
            this.method_ResourceKey_create = class_ResourceKey.getDeclaredMethod("a", class_ResourceKey, class_ResourceLocation);
            Class<?> class_StructureStart = Class.forName("net.minecraft.world.level.levelgen.structure.StructureStart");
            this.field_StructureStart_INVALID_START = class_StructureStart.getField("b");
        }

        @Override
        public boolean isWithinStructure(Location location, NamespacedKey namespacedKey) throws ReflectiveOperationException {
            Object serverLevel = this.method_CraftWorld_getHandle.invoke(location.getWorld());
            Object structureManager = this.method_ServerLevel_structureManager.invoke(serverLevel);
            Object blockPos = this.constructor_BlockPos.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object registryAccess = this.method_ServerLevel_registryAccess.invoke(serverLevel);
            Object resourceLocation = this.method_ResourceLocation_parse.invoke(null, namespacedKey.toString());
            Object structureRegistryKey = this.field_Registries_STRUCTURE.get(null);
            Object resourceKey = this.method_ResourceKey_create.invoke(null, structureRegistryKey, resourceLocation);
            Object structureRegistry = this.method_RegistryAccess_registryOrThrow.invoke(registryAccess, structureRegistryKey);
            Object structure = this.method_Registry_getOrThrow.invoke(structureRegistry, resourceKey);
            Object structureStart = this.method_StructureManager_getStructureWithPieceAt.invoke(structureManager, blockPos, structure);
            Object invalidStart = this.field_StructureStart_INVALID_START.get(null);
            return !structureStart.equals(invalidStart);
        }

    }

    private static class StructureHandler_v1_17 implements StructureHandler {

        private final BiMap<?, ?> structures;
        private final Method method_CraftWorld_getHandle;
        private final Constructor<?> constructor_BlockPosition;
        private final Method method_WorldServer_getStructureManager;
        private final Method method_StructureManager_a;
        private final Method method_StructureStart_e;

        public StructureHandler_v1_17() throws ReflectiveOperationException {
            String name = Bukkit.getServer().getClass().getPackage().getName();

            Class<?> class_StructureGenerator = Class.forName("net.minecraft.world.level.levelgen.feature.StructureGenerator");
            Class<?> class_CraftWorld = Class.forName(name + ".CraftWorld");
            Class<?> class_BlockPosition = Class.forName("net.minecraft.core.BlockPosition");
            Class<?> class_WorldServer = Class.forName("net.minecraft.server.level.WorldServer");
            Class<?> class_StructureManager = Class.forName("net.minecraft.world.level.StructureManager");
            Class<?> class_StructureStart = Class.forName("net.minecraft.world.level.levelgen.structure.StructureStart");
            this.method_WorldServer_getStructureManager = class_WorldServer.getDeclaredMethod("getStructureManager");

            Field field_StructureGenerator_a = class_StructureGenerator.getDeclaredField("a");
            this.structures = (BiMap<?, ?>) field_StructureGenerator_a.get(null);
            this.method_CraftWorld_getHandle = class_CraftWorld.getDeclaredMethod("getHandle");
            this.constructor_BlockPosition = class_BlockPosition.getConstructor(int.class, int.class, int.class);
            this.method_StructureManager_a = class_StructureManager.getDeclaredMethod("a", class_BlockPosition, boolean.class, class_StructureGenerator);
            this.method_StructureStart_e = class_StructureStart.getDeclaredMethod("e");
        }

        @Override
        public boolean isWithinStructure(Location location, NamespacedKey structureKey) throws ReflectiveOperationException {
            World world = location.getWorld();
            if (world == null)
                return false;

            Object structureGenerator = this.structures.get(structureKey.getKey());
            Object nmsWorld = this.method_CraftWorld_getHandle.invoke(world);
            Object blockPosition = this.constructor_BlockPosition.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object structureManager = this.method_WorldServer_getStructureManager.invoke(nmsWorld);
            Object structureStart = this.method_StructureManager_a.invoke(structureManager, blockPosition, true, structureGenerator);
            return (boolean) this.method_StructureStart_e.invoke(structureStart);
        }

    }

    private static class StructureHandler_v1_16 implements StructureHandler {

        private final BiMap<?, ?> structures;
        private final Method method_CraftWorld_getHandle;
        private final Constructor<?> constructor_BlockPosition;
        private final Method method_WorldServer_getStructureManager;
        private final Method method_StructureManager_a;
        private final Method method_StructureStart_e;

        public StructureHandler_v1_16() throws ReflectiveOperationException {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String version = name.substring(name.lastIndexOf('.') + 1);

            Class<?> class_StructureGenerator = Class.forName("net.minecraft.server." + version + ".StructureGenerator");
            Class<?> class_CraftWorld = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");
            Class<?> class_BlockPosition = Class.forName("net.minecraft.server." + version + ".BlockPosition");
            Class<?> class_WorldServer = Class.forName("net.minecraft.server." + version + ".WorldServer");
            Class<?> class_StructureManager = Class.forName("net.minecraft.server." + version + ".StructureManager");
            Class<?> class_StructureStart = Class.forName("net.minecraft.server." + version + ".StructureStart");
            this.method_WorldServer_getStructureManager = class_WorldServer.getDeclaredMethod("getStructureManager");

            Field field_StructureGenerator_a = class_StructureGenerator.getDeclaredField("a");
            this.structures = (BiMap<?, ?>) field_StructureGenerator_a.get(null);
            this.method_CraftWorld_getHandle = class_CraftWorld.getDeclaredMethod("getHandle");
            this.constructor_BlockPosition = class_BlockPosition.getConstructor(int.class, int.class, int.class);
            this.method_StructureManager_a = class_StructureManager.getDeclaredMethod("a", class_BlockPosition, boolean.class, class_StructureGenerator);
            this.method_StructureStart_e = class_StructureStart.getDeclaredMethod("e");
        }

        @Override
        public boolean isWithinStructure(Location location, NamespacedKey structureKey) throws ReflectiveOperationException {
            World world = location.getWorld();
            if (world == null)
                return false;

            Object structureGenerator = this.structures.get(structureKey.getKey());
            Object nmsWorld = this.method_CraftWorld_getHandle.invoke(world);
            Object blockPosition = this.constructor_BlockPosition.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object structureManager = this.method_WorldServer_getStructureManager.invoke(nmsWorld);
            Object structureStart = this.method_StructureManager_a.invoke(structureManager, blockPosition, true, structureGenerator);
            return (boolean) this.method_StructureStart_e.invoke(structureStart);
        }

    }

}
