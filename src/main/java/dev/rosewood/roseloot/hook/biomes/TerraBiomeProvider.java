package dev.rosewood.roseloot.hook.biomes;

import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Terra support is added through reflection since it requires Java 17, and we want to keep compatibility with Java 8.
 */
public class TerraBiomeProvider extends BiomeProvider {

    private Method adaptServerWorld, adaptVector3;
    private Method getSeed, getBiomeProvider, getBiome, getID;

    public TerraBiomeProvider(String pluginName) {
        super(pluginName);
    }

    @Override
    protected boolean checkEnabled(String pluginName) {
        if (!super.checkEnabled(pluginName))
            return false;

        try {
            Class<?> classBukkitAdapter = Class.forName("com.dfsek.terra.bukkit.world.BukkitAdapter");
            this.adaptServerWorld = classBukkitAdapter.getDeclaredMethod("adapt", World.class);
            Class<?> classWorldProperties = Class.forName("com.dfsek.terra.api.world.info.WorldProperties");
            Class<?> classWorld = Class.forName("com.dfsek.terra.api.world.World");
            this.adaptVector3 = classBukkitAdapter.getDeclaredMethod("adapt", Location.class);
            Class<?> classVector3 = Class.forName("com.dfsek.terra.api.util.vector.Vector3");
            this.getSeed = classWorldProperties.getDeclaredMethod("getSeed");
            this.getBiomeProvider = classWorld.getDeclaredMethod("getBiomeProvider");
            Class<?> classBiomeProvider = Class.forName("com.dfsek.terra.api.world.biome.generation.BiomeProvider");
            this.getBiome = classBiomeProvider.getDeclaredMethod("getBiome", classVector3, long.class);
            Class<?> classStringIdentifiable = Class.forName("com.dfsek.terra.api.registry.key.StringIdentifiable");
            this.getID = classStringIdentifiable.getDeclaredMethod("getID");
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getBiomeName(Location location) {
        World world = location.getWorld();
        if (world == null)
            return null;

        try {
            Object terraWorld = this.adaptServerWorld.invoke(null, world);
            long seed = (long) this.getSeed.invoke(terraWorld);
            Object biomeProvider = this.getBiomeProvider.invoke(terraWorld);
            Object vector3 = this.adaptVector3.invoke(null, location);
            Object biome = this.getBiome.invoke(biomeProvider, vector3, seed);
            return (String) this.getID.invoke(biome);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
