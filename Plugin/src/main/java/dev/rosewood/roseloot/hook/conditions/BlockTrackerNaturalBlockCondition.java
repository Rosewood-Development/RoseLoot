package dev.rosewood.roseloot.hook.conditions;

import dev.krakenied.blocktracker.bukkit.BukkitBlockTrackerAPI;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Optional;
import org.bukkit.block.Block;

public class BlockTrackerNaturalBlockCondition extends BaseLootCondition {

    public BlockTrackerNaturalBlockCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Block> optionalBlock = context.get(LootContextParams.LOOTED_BLOCK);
        if (optionalBlock.isEmpty())
            return false;

        Block block = optionalBlock.get();
        return !BukkitBlockTrackerAPI.isTracked(block);
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
