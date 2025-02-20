package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import net.advancedplugins.items.Core;
import net.advancedplugins.items.api.AdvancedItemsAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdvancedItemsProvider extends ItemProvider {

    public AdvancedItemsProvider() {
        super("AdvancedItems", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        Player player = context.getLootingPlayer().orElse(null);
        net.advancedplugins.items.objects.CustomItem customItem = Core.getItemsHandler().getCustomItem(id);
        ItemStack[] itemStacks = customItem.getItem(player, 1);
        if (itemStacks.length == 0)
            return null;

        return itemStacks[0];
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return AdvancedItemsAPI.getCustomItemName(item);
    }

}
