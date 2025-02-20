package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.ItemStack;

public class ItemEditItemProvider extends ItemProvider {

    public ItemEditItemProvider() {
        super("ItemEdit", false);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        return ItemEdit.get().getServerStorage().getItem(id);
    }

}
