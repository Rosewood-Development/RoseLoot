package dev.rosewood.roseloot.hook.items;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class SlimefunItemProvider implements ItemProvider {

    private final boolean enabled;

    public SlimefunItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Slimefun");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        SlimefunItem item = SlimefunItem.getById(id);
        if (item == null)
            return null;

        return item.getItem().clone();
    }

}
