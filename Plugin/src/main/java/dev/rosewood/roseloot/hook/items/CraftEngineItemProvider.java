package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.inventory.ItemStack;

public class CraftEngineItemProvider extends ItemProvider {

    public CraftEngineItemProvider() {
        super("CraftEngine", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled()) return null;
        BukkitItemDefinition itemStackCustomItem = CraftEngineItems.byId(id);
        return itemStackCustomItem == null ? null : itemStackCustomItem.buildBukkitItem();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled()) return null;
        Key customItemId = CraftEngineItems.getCustomItemId(item);
        return customItemId == null ? null : customItemId.asString();
    }
}
