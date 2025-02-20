package dev.rosewood.roseloot.hook.items;

import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EcoItemProvider extends ItemProvider {

    public EcoItemProvider() {
        super("eco", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        ItemStack item = Items.lookup(id).getItem();
        return item.getType() != Material.AIR ? item : null;
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        CustomItem customItem = Items.getCustomItem(item);
        if (customItem == null)
            return null;

        return customItem.getKey().toString();
    }

}
