package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class TemperatureCondition extends BaseLootCondition {

    private double temperature;

    public TemperatureCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getBlock)
                .map(Block::getTemperature)
                .filter(x -> x >= this.temperature)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length == 0)
            return false;

        try {
            String value = values[0];
            this.temperature = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
