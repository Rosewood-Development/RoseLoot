package dev.rosewood.roseloot.hook.conditions.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTTileEntity;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;

public class NBTBlockCondition extends NBTCondition {

    public NBTBlockCondition(String tag) {
        super(tag);
    }

    @Override
    protected NBTCompound getNBTCompound(LootContext context) {
        return context.get(LootContextParams.LOOTED_BLOCK)
                .map(Block::getState)
                .filter(TileState.class::isInstance)
                .map(NBTTileEntity::new)
                .orElse(null);
    }

}
