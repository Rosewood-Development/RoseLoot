package dev.rosewood.roseloot.hook.items;

import com.jojodmo.itembridge.ItemBridge;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * Supports CustomItems and any other plugins that hook with the ItemBridge API.
 */
public class ItemBridgeItemProvider implements ItemProvider {

    private final boolean enabled;

    public ItemBridgeItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ItemBridge");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        return ItemBridge.getItemStack(id);
    }

}
