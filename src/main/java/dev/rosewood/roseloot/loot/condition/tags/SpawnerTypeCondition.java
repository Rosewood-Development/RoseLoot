package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
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
        return context.get(LootContextParams.LOOTED_BLOCK)
                .map(Block::getState)
                .map(x -> x instanceof CreatureSpawner ? ((CreatureSpawner) x) : null)
                .map(CreatureSpawner::getSpawnedType)
                .filter(x -> this.entityTypes.contains(x))
                .isPresent();
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
