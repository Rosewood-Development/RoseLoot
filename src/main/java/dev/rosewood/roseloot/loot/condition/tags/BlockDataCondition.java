package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        Optional<Block> block = context.get(LootContextParams.LOOTED_BLOCK);
        if (block.isEmpty())
            return false;

        BlockData blockData = block.get().getBlockData();
        String blockDataString = blockData.getAsString(false);
        if (!blockDataString.contains("[") || !blockDataString.contains("]"))
            return false;

        blockDataString = blockDataString.substring(blockDataString.indexOf('[') + 1, blockDataString.lastIndexOf(']')).replaceAll(" ", "").toLowerCase(); // Remove [] and all spaces
        List<String> dataValues = List.of(blockDataString.split(","));
        return this.blockData.stream().anyMatch(dataValues::contains);
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockData = Arrays.stream(values).map(String::toLowerCase).collect(Collectors.toList());
        return !this.blockData.isEmpty();
    }

}
