package dev.rosewood.roseloot.hook.items;

import com.jojodmo.itembridge.ItemBridge;
import com.jojodmo.itembridge.ItemBridgeKey;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

/**
 * Supports CustomItems and any other plugins that hook with the ItemBridge API.
 */
public class ItemBridgeItemProvider extends ItemProvider {

    public ItemBridgeItemProvider() {
        super("ItemBridge", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        return ItemBridge.getItemStack(id);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        ItemBridgeKey key = ItemBridge.getItemKey(item);
        if (key == null)
            return null;

        return key.toString();
    }

}
