package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * relative-block-type:north,stone
 * value 1: The relative block to check
 * value 2+: A block type
 */
public class RelativeBlockTypeCondition extends BaseLootCondition {

    private BlockFace blockFace;
    private Set<Material> blockTypes;

    public RelativeBlockTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getBlock)
                .map(x -> x.getRelative(this.blockFace))
                .map(Block::getType)
                .filter(this.blockTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length < 2)
            return false;

        try {
            this.blockFace = BlockFace.valueOf(values[0].toUpperCase());
        } catch (Exception e) {
            return false;
        }

        this.blockTypes = EnumSet.noneOf(Material.class);

        for (int i = 1; i < values.length; i++) {
            try {
                String value = values[i];
                if (value.startsWith("#")) {
                    Set<Material> tagBlocks = LootUtils.getTagValues(value.substring(1), Material.class, "blocks");
                    if (tagBlocks != null) {
                        this.blockTypes.addAll(tagBlocks);
                        continue;
                    }
                }

                Material blockMaterial = Material.matchMaterial(value);
                if (blockMaterial != null && blockMaterial.isBlock())
                    this.blockTypes.add(blockMaterial);
            } catch (Exception ignored) { }
        }

        return !this.blockTypes.isEmpty();
    }

}
