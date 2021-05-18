package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.World;

public class WorldCondition extends LootCondition {

    private List<String> worlds;

    public WorldCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        World world = context.getLocation().getWorld();
        if (world == null)
            return false;
        return this.worlds.stream().anyMatch(x -> x.equalsIgnoreCase(world.getName()));
    }

    @Override
    public boolean parseValues(String[] values) {
        this.worlds = new ArrayList<>(Arrays.asList(values));
        return !this.worlds.isEmpty();
    }

}
