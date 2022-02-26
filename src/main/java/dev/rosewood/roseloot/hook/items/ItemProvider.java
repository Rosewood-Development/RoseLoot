package dev.rosewood.roseloot.hook.items;

import org.bukkit.inventory.ItemStack;

public interface ItemProvider {

    /**
     * Gets the ItemStack for the given item ID.
     *
     * @param id The item ID to look up
     * @return The ItemStack for the given item ID, or null if no item with the ID could be found
     */
    ItemStack getItem(String id);

}
