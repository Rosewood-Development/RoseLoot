package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class RandomNumberLootItem implements AutoTriggerableLootItem {

    private final String id;
    private final NumberProvider numberProvider;

    public RandomNumberLootItem(String id, NumberProvider numberProvider) {
        this.id = id;
        this.numberProvider = numberProvider;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        double doubleValue = this.numberProvider.getDouble(context);
        int intValue = (int) Math.round(doubleValue);

        if (this.id.isBlank()) {
            context.addPlaceholder("random_number_int", intValue);
            context.addPlaceholder("random_number_double", doubleValue);
        } else {
            context.addPlaceholder("random_number_" + this.id + "_int", intValue);
            context.addPlaceholder("random_number_" + this.id + "_double", doubleValue);
        }
    }

    public static RandomNumberLootItem fromSection(ConfigurationSection section) {
        String id = section.getString("id", "");
        NumberProvider number = NumberProvider.fromSection(section, "number", 0);
        return new RandomNumberLootItem(id, number);
    }

}
