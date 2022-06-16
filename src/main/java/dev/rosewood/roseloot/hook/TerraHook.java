package dev.rosewood.roseloot.hook;

import com.dfsek.terra.api.world.World;
import com.dfsek.terra.api.world.biome.Biome;
import com.dfsek.terra.bukkit.world.BukkitAdapter;
import dev.rosewood.roseloot.hook.biomes.TerraBiomeProvider;
import org.bukkit.Location;

/**
 * This exists for the {@link TerraBiomeProvider} class to not throw a NoClassDefFoundError on initialization.
 */
public class TerraHook {

    public static String getBiomeID(org.bukkit.World world, Location location) {
        World terraWorld = BukkitAdapter.adapt(world);
        Biome terraBiome = terraWorld.getBiomeProvider().getBiome(BukkitAdapter.adapt(location), terraWorld.getSeed());
        return terraBiome.getID();
    }

}
