package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public class DimensionCondition extends BaseLootCondition {

    private List<World.Environment> dimensions;

    public DimensionCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .map(World::getEnvironment)
                .filter(this.dimensions::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.dimensions = new ArrayList<>();

        for (String value : values) {
            try {
                this.dimensions.add(World.Environment.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.dimensions.isEmpty();
    }

}
