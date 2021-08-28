package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;

/**
 * @param <T> The type created by this LootItem
 */
public interface LootItem<T> {

    /**
     * Creates the contents produced by this LootItem
     *
     * @param context The LootContext
     * @return the created contents
     */
    T create(LootContext context);

    /**
     * Attempts to combine another LootItem into this LootItem.
     * Should only return {@code true} if a combination has occurred.
     *
     * @param lootItem The LootItem to attempt to merge with
     * @return true if a combination has occurred, otherwise false if nothing happened
     */
    default boolean combineWith(LootItem<?> lootItem) {
        return false;
    }

}
