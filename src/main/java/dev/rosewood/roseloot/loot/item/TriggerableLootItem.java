package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.Location;

public interface TriggerableLootItem<T> extends LootItem<T> {

    /**
     * Triggers the LootItem to run using the contents created by {@link LootItem#create(LootContext)}
     *
     * @param context The LootContext
     * @param location The Location to trigger at
     */
    void trigger(LootContext context, Location location);

}
