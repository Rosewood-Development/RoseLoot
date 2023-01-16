package dev.rosewood.roseloot.loot.item;

/**
 * Represents an item that can generate loot.
 * <br>
 * For implementation, see the following interfaces:
 * <ul>
 *     <li>{@link ItemGenerativeLootItem} to generate items to drop.</li>
 *     <li>{@link ExperienceGenerativeLootItem} to generate experience to drop.</li>
 *     <li>{@link TriggerableLootItem} to trigger something to happen.</li>
 *     <li>{@link RecursiveLootItem} to generate additional LootItems.</li>
 * </ul>
 */
public sealed interface LootItem permits ItemGenerativeLootItem, ExperienceGenerativeLootItem, TriggerableLootItem, RecursiveLootItem {

    /**
     * Attempts to combine another LootItem into this LootItem.
     * Should only return {@code true} if a combination has occurred.
     *
     * @param lootItem The LootItem to attempt to merge with
     * @return true if a combination has occurred, otherwise false if nothing happened
     */
    default boolean combineWith(LootItem lootItem) {
        return false;
    }

}
