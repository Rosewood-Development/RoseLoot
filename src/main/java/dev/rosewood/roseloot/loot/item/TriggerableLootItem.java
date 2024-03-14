package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.Location;

/**
 * Represents a LootItem that can trigger its contents.
 */
public non-sealed interface TriggerableLootItem extends LootItem {

    /**
     * Causes the LootItem to trigger its contents
     *
     * @param context The LootContext
     * @param location The Location to trigger at
     */
    void trigger(LootContext context, Location location);

}
