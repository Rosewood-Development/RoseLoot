package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;
import org.bukkit.Location;

/**
 * Represents a LootItem that can trigger its contents as a group with compatible GroupTriggerableLootItems.
 */
public interface GroupTriggerableLootItem<T extends GroupTriggerableLootItem<T>> extends TriggerableLootItem {

    @Override
    default void trigger(LootContext context, Location location) {
        this.trigger(context, location, List.of());
    }

    /**
     * Causes the LootItem to trigger its contents.
     * Allows for triggering multiple compatible GroupTriggerableLootItems at once.
     *
     * @param context The LootContext
     * @param location The Location to trigger at
     * @param others The list of other compatible GroupTriggerableLootItems that have been verified to be compatible using {@link #canTriggerWith}
     */
    void trigger(LootContext context, Location location, List<T> others);

    /**
     * Checks if this GroupTriggerableLootItem can trigger with the given other GroupTriggerableLootItem.
     *
     * @param other The other GroupTriggerableLootItem
     * @return true if this GroupTriggerableLootItem can trigger with the given other GroupTriggerableLootItem
     */
    boolean canTriggerWith(T other);

}
