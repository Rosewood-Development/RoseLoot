package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ZItemProvider extends ItemProvider {

    public ZItemProvider() {
        super("zItems", false);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        ItemPlugin itemPlugin = (ItemPlugin) Bukkit.getPluginManager().getPlugin("zItems");
        if (itemPlugin == null)
            return null;

        ItemManager itemManager = itemPlugin.getItemManager();
        return itemManager.getItem(id)
                .map(item -> item.build(context.getLootingPlayer().orElse(null), 1))
                .orElse(null);
    }

}
