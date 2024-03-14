package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class ExplosionLootItem implements GroupTriggerableLootItem<ExplosionLootItem> {

    private final NumberProvider power;
    private final boolean fire;
    private final boolean breakBlocks;

    public ExplosionLootItem(NumberProvider power, boolean fire, boolean breakBlocks) {
        this.power = power;
        this.fire = fire;
        this.breakBlocks = breakBlocks;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        this.trigger(context, location, List.of());
    }

    @Override
    public void trigger(LootContext context, Location location, List<ExplosionLootItem> others) {
        int power = this.power.getInteger(context) + others.stream().mapToInt(x -> x.power.getInteger(context)).sum();
        boolean fire = this.fire || others.stream().anyMatch(x -> x.fire);
        boolean breakBlocks = this.breakBlocks || others.stream().anyMatch(x -> x.breakBlocks);
        location.getWorld().createExplosion(location, power, fire, breakBlocks);
    }

    @Override
    public boolean canTriggerWith(ExplosionLootItem other) {
        return true;
    }

    public static ExplosionLootItem fromSection(ConfigurationSection section) {
        NumberProvider power = NumberProvider.fromSection(section, "power", 3);
        boolean fire = section.getBoolean("fire", false);
        boolean breakBlocks = section.getBoolean("break-blocks", true);
        return new ExplosionLootItem(power, fire, breakBlocks);
    }

}
