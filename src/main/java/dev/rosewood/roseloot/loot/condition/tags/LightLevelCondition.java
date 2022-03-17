package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class LightLevelCondition extends LootCondition {

    private int lightLevel;

    public LightLevelCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Optional<Block> originBlock = context.get(LootContextParams.ORIGIN).map(Location::getBlock);
        if (!originBlock.isPresent())
            return false;

        int light = originBlock.get().getLightLevel();
        if (light == 0) {
            // Sometimes a block can have a light level of 0 if it's not a full block, try to check the block above it too
            light = originBlock.get().getRelative(BlockFace.UP).getLightLevel();
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
