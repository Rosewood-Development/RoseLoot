package dev.rosewood.roseloot.hook.biomes;

import dev.rosewood.roseloot.hook.TerraHook;
import org.bukkit.Location;

/**
 * Terra support is added through reflection since it requires Java 17, and we want to keep compatibility with Java 8.
 */
public class TerraBiomeProvider extends BiomeProvider {

    public TerraBiomeProvider(String pluginName) {
        super(pluginName);
    }

    @Override
    public String getBiomeName(Location location) {
        org.bukkit.World world = location.getWorld();
        if (world == null)
            return null;
        return TerraHook.getBiomeID(world, location);
    }

}
