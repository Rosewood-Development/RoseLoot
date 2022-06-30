package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.inventory.ItemStack;

public class SlimefunItemProvider extends ItemProvider {

    public SlimefunItemProvider() {
        super("Slimefun");
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        SlimefunItem item = SlimefunItem.getById(id);
        if (item == null)
            return null;

        return item.getItem().clone();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
        if (slimefunItem == null)
            return null;

        return slimefunItem.getId();
    }

}
