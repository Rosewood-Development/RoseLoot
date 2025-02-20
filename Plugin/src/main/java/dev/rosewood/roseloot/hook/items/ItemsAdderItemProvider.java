package dev.rosewood.roseloot.hook.items;

import dev.lone.itemsadder.api.CustomStack;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderItemProvider extends ItemProvider {

    public ItemsAdderItemProvider() {
        super("ItemsAdder", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        CustomStack customStack = CustomStack.getInstance(id);
        if (customStack == null)
            return null;

        return customStack.getItemStack();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack == null)
            return null;

        return customStack.getId();
    }

}
