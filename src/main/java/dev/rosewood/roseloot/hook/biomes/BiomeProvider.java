package dev.rosewood.roseloot.hook.biomes;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class BiomeProvider {

    private final boolean enabled;

    public BiomeProvider(String pluginName) {
        this.enabled = this.checkEnabled(pluginName);
    }

    /**
     * Checks if the plugin for this provider is enabled
     *
     * @param pluginName The name of the plugin to check
     * @return true if the plugin is enabled, false otherwise
     */
    protected boolean checkEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
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
