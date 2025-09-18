package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootItemGenerator;
import dev.rosewood.roseloot.loot.context.LootContext;

/**
 * Represents a LootItem that generates additional LootItems.
 */
public non-sealed interface RecursiveLootItem extends LootItem, LootItemGenerator<LootItem> {

    /**
     * Checks if this recursive loot item passes conditions
     *
     * @param context The LootContext
     * @return true if conditions pass, false otherwise
     */
    boolean check(LootContext context);

}
