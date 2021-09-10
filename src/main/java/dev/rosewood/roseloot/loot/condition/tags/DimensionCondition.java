package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public class DimensionCondition extends LootCondition {

    private List<World.Environment> dimensions;

    public DimensionCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Location location = context.getLocation();
        World world = location.getWorld();
        if (world == null)
            return false;

        return this.dimensions.contains(world.getEnvironment());
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
