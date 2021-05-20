package dev.rosewood.roseloot.loot.item.meta;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemLootMeta {

    public ItemLootMeta() {

    }

    /**
     * Applies stored ItemMeta information to the given ItemStack
     *
     * @param itemStack The ItemStack to apply ItemMeta to
     * @return The same ItemStack
     */
    public ItemStack apply(ItemStack itemStack) {
        return itemStack;
    }

    public static ItemLootMeta fromSection(Material material, ConfigurationSection section) {
        return new ItemLootMeta(); // TODO
    }

}
