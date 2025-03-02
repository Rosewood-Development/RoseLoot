package dev.rosewood.roseloot.nms.v1_20_R4;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.nms.NMSHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
        ServerLevel serverLevel = ((CraftWorld) world).getHandle();
        FeatureFlagSet featureFlagSet = serverLevel != null ? serverLevel.enabledFeatures() : ((CraftWorld) Bukkit.getWorlds().getFirst()).getHandle().enabledFeatures();
        RandomSource randomSource = serverLevel != null ? serverLevel.getRandom() : RandomSource.create();
        net.minecraft.world.item.ItemStack enchantedStack = EnchantmentHelper.enchantItem(featureFlagSet, randomSource, nmsStack, levels, treasure);
        return CraftItemStack.asCraftMirror(enchantedStack);
    }

}
