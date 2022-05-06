package dev.rosewood.roseloot.hook.items;

import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.ItemStack;

public class ItemEditItemProvider extends ItemProvider {

    public ItemEditItemProvider() {
        super("ItemEdit", false);
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.isEnabled())
            return null;

        return ItemEdit.get().getServerStorage().getItem(id);
    }

}
