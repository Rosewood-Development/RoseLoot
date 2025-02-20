package dev.rosewood.roseloot.hook.biomes;

import dev.rosewood.roseloot.hook.TerraHook;
import org.bukkit.Location;

public class TerraBiomeProvider extends BiomeProvider {

    public TerraBiomeProvider() {
        super("Terra");
    }

    @Override
    public String getBiomeName(Location location) {
        org.bukkit.World world = location.getWorld();
        if (world == null)
            return null;
        return TerraHook.getBiomeID(world, location);
    }

}
