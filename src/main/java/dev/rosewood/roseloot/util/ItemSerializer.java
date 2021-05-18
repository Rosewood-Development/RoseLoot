package dev.rosewood.roseloot.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public final class ItemSerializer {

    /**
     * Serializes an ItemStack to a ConfigurationSection in a user-editable format
     *
     * @param itemStack The ItemStack to serialize
     * @param section The ConfigurationSection to serialize to
     */
    public static void serialize(ItemStack itemStack, ConfigurationSection section) {
        section.set("type", itemStack.getType().name().toLowerCase());
    }

    /**
     * Deserializes an ItemStack from a ConfigurationSection
     *
     * @param section The CondigurationSection to deserialize from
     * @return the deserialized ItemStack
     */
    public static ItemStack deserialize(ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("type", "air"));
        if (material == null)
            material = Material.AIR;
        return new ItemStack(material);
    }

}
