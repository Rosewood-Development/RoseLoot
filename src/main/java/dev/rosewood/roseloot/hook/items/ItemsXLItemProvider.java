package dev.rosewood.roseloot.hook.items;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemsXLItemProvider implements ItemProvider {

    private final boolean enabled;

    public ItemsXLItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("ItemsXL");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        ExItem exItem = CaliburnAPI.getInstance().getExItem(id);
        if (exItem == null)
            return null;

        return exItem.toItemStack();
    }

}
