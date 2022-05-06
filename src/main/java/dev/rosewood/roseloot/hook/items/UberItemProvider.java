package dev.rosewood.roseloot.hook.items;

import org.bukkit.inventory.ItemStack;
import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.UberItems;
import thirtyvirus.uber.UberMaterial;

public class UberItemProvider extends ItemProvider {

    public UberItemProvider() {
        super("UberItems", false);
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.isEnabled())
            return null;

        UberItem item = UberItems.getItem(id);
        if (item != null)
            return item.makeItem(1);

        UberMaterial material = UberItems.getMaterial(id);
        if (material != null)
            return material.makeItem(1);

        return null;
    }

}
