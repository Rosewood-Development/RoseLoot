package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.NumberProvider;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class RandomNumberLootItem implements TriggerableLootItem {

    private final NumberProvider numberProvider;

    public RandomNumberLootItem(NumberProvider numberProvider) {
        this.numberProvider = numberProvider;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        double doubleValue = this.numberProvider.getDouble();
        int intValue = (int) Math.round(doubleValue);

        context.getPlaceholders().add("random_number_int", intValue);
        context.getPlaceholders().add("random_number_double", intValue);
    }

    public static RandomNumberLootItem fromSection(ConfigurationSection section) {
        NumberProvider number = NumberProvider.fromSection(section, "number", 0);
        return new RandomNumberLootItem(number);
    }

}
