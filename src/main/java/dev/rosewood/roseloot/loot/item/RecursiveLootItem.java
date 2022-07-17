package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;

/**
 * Represents a LootItem that generates additional LootItems.
 */
public non-sealed interface RecursiveLootItem extends LootItem {

    /**
     * Generates additional LootItems.
     *
     * @param context The LootContext
     * @return The LootItems to drop
     */
    List<LootItem> generate(LootContext context);

}
