package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.block.BlockFace;

public class LightLevelCondition extends LootCondition {

    private int lightLevel;

    public LightLevelCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        int light = context.getLocation().getBlock().getLightLevel();
        if (light == 0) {
            // Sometimes a block can have a light level of 0 if it's not a full block, try to check the block above it too
            light = context.getLocation().getBlock().getRelative(BlockFace.UP).getLightLevel();
        }
        return light >= this.lightLevel;
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length == 0)
            return false;

        try {
            String value = values[0];
            this.lightLevel = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
