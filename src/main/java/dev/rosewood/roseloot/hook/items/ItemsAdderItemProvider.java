package dev.rosewood.roseloot.hook.items;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderItemProvider implements ItemProvider {

    private final boolean enabled;

    public ItemsAdderItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        CustomStack customStack = CustomStack.getInstance(id);
        if (customStack == null)
            return null;

        return customStack.getItemStack();
    }

}
