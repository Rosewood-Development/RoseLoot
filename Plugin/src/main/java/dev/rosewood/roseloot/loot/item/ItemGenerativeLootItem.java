package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootItemGenerator;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a LootItem that generates items to drop.
 */
public non-sealed interface ItemGenerativeLootItem extends LootItem, LootItemGenerator<ItemStack> {

}
