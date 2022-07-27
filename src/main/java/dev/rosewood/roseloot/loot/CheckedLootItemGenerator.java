package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.LootItem;

public interface CheckedLootItemGenerator extends LootItemGenerator<LootItem> {

    /**
     * Checks if this generator passes conditions
     *
     * @param context The LootContext
     * @return true if conditions pass, false otherwise
     */
    boolean check(LootContext context);

}
