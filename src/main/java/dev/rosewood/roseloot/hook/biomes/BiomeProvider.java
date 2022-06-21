package dev.rosewood.roseloot.hook.biomes;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class BiomeProvider {

    private final boolean enabled;

    public BiomeProvider(String pluginName) {
        this.enabled = Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    /**
     * @return true if the provider is enabled, false otherwise
     */
    public final boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Gets the name of the biome at the given Location
     *
     * @param location The location to get the biome at
     * @return The name of the biome at the given Location
     */
    public abstract String getBiomeName(Location location);

}
