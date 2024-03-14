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

}
