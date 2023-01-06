package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class ExplosionLootItem implements TriggerableLootItem {

    private final List<NumberProvider> powers;
    private boolean fire;
    private boolean breakBlocks;

    public ExplosionLootItem(NumberProvider power, boolean fire, boolean breakBlocks) {
        this.powers = new ArrayList<>(List.of(power));
        this.fire = fire;
        this.breakBlocks = breakBlocks;
    }

    @Override
    public boolean combineWith(LootItem lootItem) {
        if (!(lootItem instanceof ExplosionLootItem other))
            return false;

        this.powers.addAll(other.powers);
        this.fire |= other.fire;
        this.breakBlocks |= other.breakBlocks;
        return true;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        World world = location.getWorld();
        if (world != null) {
            int power = this.powers.stream().mapToInt(x -> x.getInteger(context)).max().orElse(0);
            world.createExplosion(location, power, this.fire, this.breakBlocks);
        }
    }

    public static ExplosionLootItem fromSection(ConfigurationSection section) {
        NumberProvider power = NumberProvider.fromSection(section, "power", 3);
        boolean fire = section.getBoolean("fire", false);
        boolean breakBlocks = section.getBoolean("break-blocks", true);
        return new ExplosionLootItem(power, fire, breakBlocks);
    }

}
