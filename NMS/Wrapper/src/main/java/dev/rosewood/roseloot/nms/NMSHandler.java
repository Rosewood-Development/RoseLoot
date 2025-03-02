package dev.rosewood.roseloot.nms;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public interface NMSHandler {

    /**
     * Randomly enchants an item using vanilla logic
     *
     * @param itemStack The ItemStack to enchant
     * @param levels The level of the enchant (equivalent to enchanting table levels)
     * @param treasure Whether or not treasure enchantments will be included (ex. mending)
     * @param world The world being enhanted in, used to determine the random source, nullable
     * @return The same ItemStack
     */
    ItemStack enchantWithLevels(ItemStack itemStack, int levels, boolean treasure, World world);

    /**
     * Checks if the given Location is within a certain type of structure
     *
     * @param location The Location to check at
     * @param structureKey The namespaced key of the structure to check for
     * @return true if the Location is within a structure with the given key, false otherwise
     */
    boolean isWithinStructure(Location location, NamespacedKey structureKey);

}
