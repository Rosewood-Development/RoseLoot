package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TriggerableLootItem<T> extends LootItem<T> {

    /**
     * Triggers the LootItem to run using the contents created by {@link LootItem#create(LootContext)}
     *
     * @param context The LootContext
     * @param player The Player that triggered the loot table, nullable
     * @param location The Location to trigger at
     */
    void trigger(LootContext context, Player player, Location location);

}
