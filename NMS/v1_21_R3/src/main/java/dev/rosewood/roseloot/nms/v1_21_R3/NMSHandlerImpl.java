package dev.rosewood.roseloot.nms.v1_21_R3;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.nms.NMSHandler;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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

        RegistryAccess.Frozen registryAccess = MinecraftServer.getServer().registryAccess();
        Optional<? extends HolderSet<Enchantment>> holderOptional = treasure ? Optional.empty() : registryAccess.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        RandomSource randomSource = world != null ? ((CraftWorld) world).getHandle().getRandom() : RandomSource.create();
        net.minecraft.world.item.ItemStack enchantedStack = EnchantmentHelper.enchantItem(randomSource, nmsStack, levels, registryAccess, holderOptional);
        return CraftItemStack.asCraftMirror(enchantedStack);
    }

}
