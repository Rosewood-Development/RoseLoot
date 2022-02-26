package dev.rosewood.roseloot.hook.items;

import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class OraxenItemProvider implements ItemProvider {

    private final boolean enabled;

    public OraxenItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Oraxen");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        if (itemBuilder == null)
            return null;

        return itemBuilder.build();
    }

}
