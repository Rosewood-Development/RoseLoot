package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;

/**
 * Represents a LootItem that generates experience to drop.
 */
public non-sealed interface ExperienceGenerativeLootItem extends LootItem {

    /**
     * Generates the amount of experience to drop
     *
     * @param context The LootContext
     * @return The amount of experience to drop
     */
    int generate(LootContext context);

}
