package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Optional;

public class NeigeItemProvider extends ItemProvider {

    public NeigeItemProvider() {
        super("NeigeItems", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        Optional<Player> player = context.getLootingPlayer();
        if (player.isPresent()) {
            return ItemManager.INSTANCE.getItemStack(id, player.get());
        }

        return ItemManager.INSTANCE.getItemStack(id);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        ItemInfo itemInfo = ItemManager.INSTANCE.isNiItem(item);
        if (itemInfo == null)
            return null;

        return itemInfo.getId();
    }
}
