package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.inventory.ItemStack;

public class KnokkoCustomItemProvider extends ItemProvider {

    public KnokkoCustomItemProvider() {
        super("CustomItems", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
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
