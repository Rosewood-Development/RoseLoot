package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a LootItem that generates items to drop.
 */
public non-sealed interface ItemGenerativeLootItem extends LootItem {

    /**
     * Generates the items to drop
     *
     * @param context The LootContext
     * @return The items to drop
     */
    List<ItemStack> generate(LootContext context);

}
