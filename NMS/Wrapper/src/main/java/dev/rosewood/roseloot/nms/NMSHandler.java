package dev.rosewood.roseloot.nms;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public interface NMSHandler {

    ItemStack enchantWithLevels(ItemStack itemStack, int levels, boolean treasure, World world);

}
