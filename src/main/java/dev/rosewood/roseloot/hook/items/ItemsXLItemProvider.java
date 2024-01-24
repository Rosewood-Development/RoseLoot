package dev.rosewood.roseloot.hook.items;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public class ItemsXLItemProvider extends ItemProvider {

    public ItemsXLItemProvider() {
        super("ItemsXL", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        ExItem exItem = CaliburnAPI.getInstance().getExItem(id);
        if (exItem == null)
            return null;

        return exItem.toItemStack();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        ExItem exItem = CaliburnAPI.getInstance().getExItem(item);
        if (exItem == null)
            return null;

        return exItem.getId();
    }

}
