package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootItemGenerator;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a LootItem that generates additional LootItems.
 */
public non-sealed interface RecursiveLootItem extends LootItem, LootItemGenerator<LootItem> {

    @Override
    default List<ItemStack> getAllItems(LootContext context) {
        return List.of();
    }

}
