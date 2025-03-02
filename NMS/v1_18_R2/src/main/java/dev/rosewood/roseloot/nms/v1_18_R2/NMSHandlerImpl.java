package dev.rosewood.roseloot.nms.v1_18_R2;

import dev.rosewood.roseloot.nms.NMSHandler;
import java.util.Random;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public ItemStack enchantWithLevels(ItemStack itemStack, int levels, boolean treasure, World world) {
        if (itemStack.getType() == Material.ENCHANTED_BOOK)
            itemStack.setType(Material.BOOK);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        Random random = world != null ? ((CraftWorld) world).getHandle().getRandom() : new Random();
        net.minecraft.world.item.ItemStack enchantedStack = EnchantmentHelper.enchantItem(random, nmsStack, levels, treasure);
        return CraftItemStack.asCraftMirror(enchantedStack);
    }

}
