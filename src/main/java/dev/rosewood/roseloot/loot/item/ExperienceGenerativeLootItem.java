package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;

/**
 * Represents a LootItem that generates experience to drop.
 */
public non-sealed interface ExperienceGenerativeLootItem<T extends ExperienceGenerativeLootItem<T>> extends LootItem {

    /**
     * Generates the amount of experience to drop
     *
     * @param context The LootContext
     * @param others The list of other ExperienceGenerativeLootItem of the same type
     * @return The amount of experience to drop
     */
    int generate(LootContext context, List<T> others);

}
