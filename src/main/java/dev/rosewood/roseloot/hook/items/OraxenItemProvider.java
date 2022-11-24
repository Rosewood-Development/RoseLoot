package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class OraxenItemProvider extends ItemProvider {

    public OraxenItemProvider() {
        super("Oraxen");
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        if (itemBuilder == null)
            return null;

        return itemBuilder.build();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return OraxenItems.getIdByItem(item);
    }

}
