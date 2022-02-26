package dev.rosewood.roseloot.hook.items;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.UberItems;
import thirtyvirus.uber.UberMaterial;

public class UberItemProvider implements ItemProvider {

    private final boolean enabled;

    public UberItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("UberItems");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
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
