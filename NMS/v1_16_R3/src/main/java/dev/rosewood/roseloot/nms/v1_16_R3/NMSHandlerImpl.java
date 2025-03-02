package dev.rosewood.roseloot.nms.v1_16_R3;

import dev.rosewood.roseloot.nms.NMSHandler;
import java.util.Random;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EnchantmentManager;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import net.minecraft.server.v1_16_R3.StructureManager;
import net.minecraft.server.v1_16_R3.StructureStart;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public ItemStack enchantWithLevels(ItemStack itemStack, int levels, boolean treasure, World world) {
        if (itemStack.getType() == Material.ENCHANTED_BOOK)
            itemStack.setType(Material.BOOK);

        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        Random random = world != null ? ((CraftWorld) world).getHandle().getRandom() : new Random();
        net.minecraft.server.v1_16_R3.ItemStack enchantedStack = EnchantmentManager.a(random, nmsStack, levels, treasure);
        return CraftItemStack.asCraftMirror(enchantedStack);
    }

    @Override
    public boolean isWithinStructure(Location location, NamespacedKey structureKey) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        StructureManager structureFeatureManager = worldServer.getStructureManager();
        BlockPosition blockPos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        StructureGenerator<?> structureGenerator = StructureGenerator.a.get(structureKey.getKey());
        if (structureGenerator == null)
            return false;

        StructureStart<?> structureStart = structureFeatureManager.a(blockPos, true, structureGenerator);
        return !structureStart.equals(StructureStart.a);
    }

}
