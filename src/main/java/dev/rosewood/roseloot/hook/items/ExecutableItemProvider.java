package dev.rosewood.roseloot.hook.items;

import com.ssomar.executableitems.api.ExecutableItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ExecutableItemProvider implements ItemProvider {

    private final boolean enabled;

    public ExecutableItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ExecutableItems");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        return ExecutableItemsAPI.getExecutableItem(id);
    }

}
