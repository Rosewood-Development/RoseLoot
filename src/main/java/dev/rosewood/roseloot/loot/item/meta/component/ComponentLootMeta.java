package dev.rosewood.roseloot.loot.item.meta.component;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.meta.ItemMeta;

public interface ComponentLootMeta {

    void apply(ItemMeta itemMeta, LootContext context);

}
