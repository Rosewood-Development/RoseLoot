package dev.rosewood.roseloot.hook.conditions.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import dev.rosewood.roseloot.loot.context.LootContext;

public class NBTItemCondition extends NBTCondition {

    public NBTItemCondition(String tag) {
        super(tag);
    }

    @Override
    protected NBTCompound getNBTCompound(LootContext context) {
        return context.getItemUsed()
                .map(NBTItem::new)
                .orElse(null);
    }

}
