package dev.rosewood.roseloot.hook.items;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public abstract class ItemProvider {

    private final boolean enabled;
    private final boolean supportsIdLookup;

    public ItemProvider(String pluginName, boolean supportsIdLookup) {
        this.enabled = this.checkEnabled(pluginName);
        this.supportsIdLookup = supportsIdLookup;
    }

    public ItemProvider(String pluginName) {
        this(pluginName, true);
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
     * @return true if the provider supports looking up an item ID from an ItemStack, false otherwise
     */
    public final boolean supportsIdLookup() {
        return this.supportsIdLookup;
    }

    /**
     * Gets the ItemStack for the given item ID.
     *
     * @param id The item ID to look up
     * @return The ItemStack for the given item ID, or null if no item with the ID could be found
     */
    public abstract ItemStack getItem(String id);

    /**
     * Gets the item ID for the given ItemStack.
     *
     * @param item The ItemStack to look up
     * @return The item ID for the given ItemStack, or null if no item ID matching the ItemStack could be found
     */
    public String getItemId(ItemStack item) {
        return null;
    }

}
