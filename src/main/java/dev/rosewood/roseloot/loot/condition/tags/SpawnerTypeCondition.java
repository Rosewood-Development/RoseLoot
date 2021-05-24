package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class SpawnerTypeCondition extends LootCondition {

    private List<EntityType> entityTypes;

    public SpawnerTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Block block = context.getLootedBlock();
        if (block == null || !(block.getState() instanceof CreatureSpawner))
            return false;

        return this.entityTypes.contains(((CreatureSpawner) block.getState()).getSpawnedType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.entityTypes = new ArrayList<>();

        for (String value : values) {
            try {
                this.entityTypes.add(EntityType.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.entityTypes.isEmpty();
    }

}
