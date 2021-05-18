package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockTypeCondition extends LootCondition {

    private List<Material> blockTypes;

    public BlockTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Block block = context.getLootedBlock();
        if (block == null)
            return false;

        return this.blockTypes.contains(block.getType());
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
