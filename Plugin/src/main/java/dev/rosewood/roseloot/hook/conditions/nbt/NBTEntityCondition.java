package dev.rosewood.roseloot.hook.conditions.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;

public class NBTEntityCondition extends NBTCondition {

    public NBTEntityCondition(String tag) {
        super(tag);
    }

    @Override
    protected NBTCompound getNBTCompound(LootContext context) {
        return context.get(LootContextParams.LOOTED_ENTITY)
                .map(NBTEntity::new)
                .orElse(null);
    }

}
