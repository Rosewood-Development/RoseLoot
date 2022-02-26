package dev.rosewood.roseloot.hook.items;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemEditItemProvider implements ItemProvider {

    private final boolean enabled;

    public ItemEditItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ItemEdit");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        return ItemEdit.get().getServerStorage().getItem(id);
    }

}
