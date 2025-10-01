package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public abstract class ItemProvider {

    private final boolean enabled;
    private final boolean supportsIdLookup;

    public ItemProvider(String pluginName, boolean supportsIdLookup) {
        this.enabled = Bukkit.getPluginManager().getPlugin(pluginName) != null;
        this.supportsIdLookup = supportsIdLookup;
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
     * @param context The LootContext
     * @param id The item ID to look up
     * @return The ItemStack for the given item ID, or null if no item with the ID could be found
     */
    public abstract ItemStack getItem(LootContext context, String id);

    /**
     * Gets the item ID for the given ItemStack
     *
     * @param item The ItemStack to look up
     * @return The item ID for the given ItemStack, or null if no item IDs matching the ItemStack could be found
     */
    public String getItemId(ItemStack item) {
        return null;
    }

    /**
     * Gets the item IDs for the given ItemStack, may include subtypes
     *
     * @param item The ItemStack to look up
     * @return The item ID for the given ItemStack, or an empty set if no item IDs matching the ItemStack could be found
     */
    public Set<String> getItemIds(ItemStack item) {
        String id = this.getItemId(item);
        if (id != null)
            return Set.of(id);
        return Set.of();
    }

    /**
     * @return a suffix to be added to the condition name
     */
    public String getConditionSuffix() {
        return "";
    }

}
