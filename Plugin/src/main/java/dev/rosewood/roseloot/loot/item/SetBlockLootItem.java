package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.RelativeTo;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.EnumHelper;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class SetBlockLootItem implements TriggerableLootItem {

    private final BlockData block;
    private final RelativeTo relativeTo;
    private final NumberProvider xOffset;
    private final NumberProvider yOffset;
    private final NumberProvider zOffset;
    private final boolean replace;

    protected SetBlockLootItem(BlockData block, RelativeTo relativeTo, NumberProvider xOffset, NumberProvider yOffset, NumberProvider zOffset, boolean replace) {
        this.block = block;
        this.relativeTo = relativeTo;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.replace = replace;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Location blockLocation = switch (this.relativeTo) {
            case LOOTER -> context.get(LootContextParams.LOOTER).map(Entity::getLocation).orElse(null);
            default -> location;
        };

        if (blockLocation == null)
            return;

        blockLocation = blockLocation.clone().add(this.xOffset.getDouble(context), this.yOffset.getDouble(context), this.zOffset.getDouble(context));

        Block block = blockLocation.getBlock();
        boolean waterlogged = block.getBlockData() instanceof Waterlogged data && data.isWaterlogged();

        if (!this.replace) {
            for (ItemStack drop : block.getDrops())
                block.getWorld().dropItemNaturally(LootUtils.adjustBlockLocation(block.getLocation()), drop);

            if (block.getState() instanceof Container container)
                for (ItemStack drop : container.getInventory().getContents())
                    if (drop != null && drop.getType() != Material.AIR)
                        block.getWorld().dropItemNaturally(LootUtils.adjustBlockLocation(block.getLocation()), drop);
        }

        BlockData blockData;
        if (waterlogged && this.block instanceof Waterlogged) {
            blockData = this.block.clone();
            ((Waterlogged) blockData).setWaterlogged(true);
        }

        block.setBlockData(this.block);
    }

    public static SetBlockLootItem fromSection(ConfigurationSection section) {
        String blockString = section.getString("block");
        if (blockString == null)
            return null;

        String blockDataString = null;
        int dataIndex = blockString.indexOf("[");
        if (dataIndex != -1) {
            blockDataString = blockString.substring(dataIndex);
            blockString = blockString.substring(0, dataIndex);
        }

        Material material = Material.matchMaterial(blockString);
        if (material == null)
            return null;

        BlockData block = material.createBlockData(blockDataString);
        RelativeTo relativeTo = EnumHelper.valueOf(RelativeTo.class, section.getString("relative-to"), RelativeTo.LOOTED);
        NumberProvider x = NumberProvider.fromSection(section, "x", 0);
        NumberProvider y = NumberProvider.fromSection(section, "y", 0);
        NumberProvider z = NumberProvider.fromSection(section, "z", 0);
        boolean replace = section.getBoolean("replace", false);

        return new SetBlockLootItem(block, relativeTo, x, y, z, replace);
    }

}
