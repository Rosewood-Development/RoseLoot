package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class AboveBlockTypeCondition extends LootCondition {

    private List<Material> blockTypes;

    public AboveBlockTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getBlock)
                .map(x -> x.getRelative(BlockFace.UP))
                .map(Block::getType)
                .filter(this.blockTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockTypes = new ArrayList<>();

        for (String value : values) {
            try {
                Material blockMaterial = Material.matchMaterial(value);
                if (blockMaterial != null && blockMaterial.isBlock())
                    this.blockTypes.add(blockMaterial);
            } catch (Exception ignored) { }
        }

        return !this.blockTypes.isEmpty();
    }

}
