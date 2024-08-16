package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.rosegarden.compatibility.CompatibilityAdapter;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class SpawnerTypeCondition extends BaseLootCondition {

    private List<EntityType> entityTypes;

    public SpawnerTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.getLootedBlockInfo()
                .map(BlockInfo::getState)
                .map(x -> x instanceof CreatureSpawner ? ((CreatureSpawner) x) : null)
                .map(x -> CompatibilityAdapter.getCreatureSpawnerHandler().getSpawnedType(x))
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
