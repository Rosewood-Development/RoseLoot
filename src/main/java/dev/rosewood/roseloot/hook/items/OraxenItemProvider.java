package dev.rosewood.roseloot.hook.items;

import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenItemProvider extends ItemProvider {

    public OraxenItemProvider() {
        super("Oraxen");
    }

    @Override
    public ItemStack getItem(String id) {
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
