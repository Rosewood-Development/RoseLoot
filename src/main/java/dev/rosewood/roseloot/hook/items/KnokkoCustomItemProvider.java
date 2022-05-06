package dev.rosewood.roseloot.hook.items;

import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.inventory.ItemStack;

public class KnokkoCustomItemProvider extends ItemProvider {

    public KnokkoCustomItemProvider() {
        super("CustomItems");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.isEnabled())
            return null;

        return CustomItemsApi.createItemStack(id, 1);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return CustomItemsApi.getItemName(item);
    }

}
