package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class WorldCondition extends LootCondition {

    private List<String> worlds;

    public WorldCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .map(x -> x.getName()) // Not using World::getName because it was changed to WorldInfo::getName which doesn't exist pre-1.17
                .filter(x -> this.worlds.stream().anyMatch(y -> y.equalsIgnoreCase(x)))
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.worlds = new ArrayList<>(List.of(values));
        return !this.worlds.isEmpty();
    }

}
