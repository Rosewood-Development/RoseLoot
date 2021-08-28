package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;

public class WeatherCondition extends LootCondition {

    private List<WeatherType> weatherTypes;

    public WeatherCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        World world = context.getLocation().getWorld();
        if (world == null)
            return false;
        return this.weatherTypes.contains(WeatherType.fromWorld(world));
    }

    @Override
    public boolean parseValues(String[] values) {
        this.weatherTypes = new ArrayList<>();

        for (String value : values) {
            try {
                WeatherType biome = WeatherType.valueOf(value.toUpperCase());
                this.weatherTypes.add(biome);
            } catch (Exception ignored) { }
        }

        return !this.weatherTypes.isEmpty();
    }

    public enum WeatherType {
        CLEAR,
        RAIN,
        STORM;

        public static WeatherType fromWorld(World world) {
            if (world.hasStorm()) {
                if (world.isThundering()) {
                    return WeatherType.STORM;
                } else {
                    return WeatherType.RAIN;
                }
            } else {
                return WeatherType.CLEAR;
            }
        }
    }

}
