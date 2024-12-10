package dev.rosewood.roseloot.hook.items;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public class NexoItemProvider extends ItemProvider {

    public NexoItemProvider() {
        super("Nexo", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        ItemBuilder itemBuilder = NexoItems.itemFromId(id);
        if (itemBuilder == null)
            return null;

        return itemBuilder.build();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return NexoItems.idFromItem(item);
    }

}
