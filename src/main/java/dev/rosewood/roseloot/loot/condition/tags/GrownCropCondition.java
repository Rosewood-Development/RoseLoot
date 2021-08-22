package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class GrownCropCondition extends LootCondition {

    public GrownCropCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        if (context.getLootedBlock() == null)
            return false;

        BlockData blockData = context.getLootedBlock().getBlockData();
        if (!(blockData instanceof Ageable))
            return false;

        Ageable ageable = (Ageable) blockData;
        return ageable.getAge() == ageable.getMaximumAge();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
