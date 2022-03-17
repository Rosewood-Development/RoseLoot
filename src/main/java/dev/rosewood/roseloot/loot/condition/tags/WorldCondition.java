package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldCondition extends LootCondition {

    private List<String> worlds;

    public WorldCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .map(World::getName)
                .filter(x -> this.worlds.stream().anyMatch(y -> y.equalsIgnoreCase(x)))
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.worlds = new ArrayList<>(Arrays.asList(values));
        return !this.worlds.isEmpty();
    }

}
