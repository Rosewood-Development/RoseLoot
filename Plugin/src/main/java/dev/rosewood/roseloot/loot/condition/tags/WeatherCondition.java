package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public class WeatherCondition extends BaseLootCondition {

    private List<WeatherType> weatherTypes;

    public WeatherCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .map(WeatherType::fromWorld)
                .filter(this.weatherTypes::contains)
                .isPresent();
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
