package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;

/**
 * Represents a LootItem that will trigger its contents automatically.
 * Primarily used for injecting placeholders into the LootContext.
 * The Location for these will always be null.
 */
public interface AutoTriggerableLootItem extends TriggerableLootItem {

    /**
     * Triggers the LootItem to trigger its contents
     *
     * @param context The LootContext
     */
    default void trigger(LootContext context) {
        this.trigger(context, null);
    }

}
