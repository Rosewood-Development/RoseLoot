package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockDataCondition extends LootCondition {

    private List<String> blockData;

    public BlockDataCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Block block = context.getLootedBlock();
        if (block == null)
            return false;

        BlockData blockData = block.getBlockData();
        String blockDataString = blockData.getAsString(false);
        blockDataString = blockDataString.substring(blockDataString.indexOf('[') + 1, blockDataString.lastIndexOf(']')).replaceAll(" ", "").toLowerCase(); // Remove [] and all spaces
        List<String> dataValues = Arrays.asList(blockDataString.split(","));
        return this.blockData.stream().anyMatch(dataValues::contains);
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockData = Arrays.stream(values).map(String::toLowerCase).collect(Collectors.toList());
        return !this.blockData.isEmpty();
    }

}
