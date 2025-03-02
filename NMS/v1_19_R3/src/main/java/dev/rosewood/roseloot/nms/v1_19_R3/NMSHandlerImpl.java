package dev.rosewood.roseloot.nms.v1_19_R3;

import dev.rosewood.roseloot.nms.NMSHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public ItemStack enchantWithLevels(ItemStack itemStack, int levels, boolean treasure, World world) {
        if (itemStack.getType() == Material.ENCHANTED_BOOK)
            itemStack.setType(Material.BOOK);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        RandomSource randomSource = world != null ? ((CraftWorld) world).getHandle().getRandom() : RandomSource.create();
        net.minecraft.world.item.ItemStack enchantedStack = EnchantmentHelper.enchantItem(randomSource, nmsStack, levels, treasure);
        return CraftItemStack.asCraftMirror(enchantedStack);
    }

    @Override
    public boolean isWithinStructure(Location location, NamespacedKey structureKey) {
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
        HolderLookup.RegistryLookup<Structure> registry = MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.STRUCTURE);
        ResourceLocation resourceLocation = new ResourceLocation(structureKey.toString());
        ResourceKey<Structure> structureResourceKey = ResourceKey.create(Registries.STRUCTURE, resourceLocation);
        Structure structure = registry.getOrThrow(structureResourceKey).value();
        StructureManager structureManager = serverLevel.structureManager();
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        StructureStart structureStart = structureManager.getStructureWithPieceAt(blockPos, structure);
        return !structureStart.equals(StructureStart.INVALID_START);
    }

}
