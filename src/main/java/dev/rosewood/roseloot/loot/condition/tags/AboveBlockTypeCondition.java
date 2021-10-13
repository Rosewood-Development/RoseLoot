package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
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
        Block block = context.getLocation().getBlock();
        Material belowType = block.getRelative(BlockFace.UP).getType();
        return this.blockTypes.contains(belowType);
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
