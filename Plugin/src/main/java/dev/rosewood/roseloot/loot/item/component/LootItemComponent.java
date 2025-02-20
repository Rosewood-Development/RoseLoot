package dev.rosewood.roseloot.loot.item.component;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public interface LootItemComponent {

    void apply(ItemStack itemStack, LootContext context);

}
