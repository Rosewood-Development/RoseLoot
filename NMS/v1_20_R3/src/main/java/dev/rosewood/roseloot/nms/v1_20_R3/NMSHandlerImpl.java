package dev.rosewood.roseloot.nms.v1_20_R3;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.nms.NMSHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public ItemStack enchantWithLevels(ItemStack itemStack, int levels, boolean treasure, World world) {
        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            if (NMSUtil.isPaper()) {
                itemStack = itemStack.withType(Material.BOOK);
            } else {
                itemStack.setType(Material.BOOK);
            }
        }

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        RandomSource randomSource = world != null ? ((CraftWorld) world).getHandle().getRandom() : RandomSource.create();
        net.minecraft.world.item.ItemStack enchantedStack = EnchantmentHelper.enchantItem(randomSource, nmsStack, levels, treasure);
        return CraftItemStack.asCraftMirror(enchantedStack);
    }

}
