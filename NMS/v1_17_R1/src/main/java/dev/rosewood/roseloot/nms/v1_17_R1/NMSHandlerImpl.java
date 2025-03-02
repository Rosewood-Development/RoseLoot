package dev.rosewood.roseloot.nms.v1_17_R1;

import dev.rosewood.roseloot.nms.NMSHandler;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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

    @Override
    public boolean isWithinStructure(Location location, NamespacedKey structureKey) {
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
        StructureFeatureManager structureFeatureManager = serverLevel.structureFeatureManager();
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        StructureFeature<?> structureFeature = StructureFeature.STRUCTURES_REGISTRY.get(structureKey.getKey());
        if (structureFeature == null)
            return false;

        StructureStart<?> structureStart = structureFeatureManager.getStructureAt(blockPos, true, structureFeature);
        return !structureStart.equals(StructureStart.INVALID_START);
    }

}
