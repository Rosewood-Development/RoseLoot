package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

public class BlockTagCondition extends LootCondition {

    private List<Tag<Material>> tags;

    public BlockTagCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Block block = context.getLootedBlock();
        if (block == null)
            return false;

        Material blockType = block.getType();
        return this.tags.stream().anyMatch(x -> x.isTagged(blockType));
    }

    @Override
    public boolean parseValues(String[] values) {
        this.tags = new ArrayList<>();

        for (String value : values) {
            for (Tag<Material> tag : Bukkit.getTags("blocks", Material.class)) {
                if (tag.getKey().getKey().equalsIgnoreCase(value)) {
                    this.tags.add(tag);
                    break;
                }
            }
        }

        return !this.tags.isEmpty();
    }

}
