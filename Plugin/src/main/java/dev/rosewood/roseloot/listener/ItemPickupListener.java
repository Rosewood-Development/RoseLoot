package dev.rosewood.roseloot.listener;

import dev.rosewood.roseloot.util.LootUtils;
import java.util.UUID;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemPickupListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onItemSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Item item))
            return;

        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        UUID owner = LootUtils.getRestrictedItemPickup(itemMeta);
        if (owner != null) {
            item.setOwner(owner);
            LootUtils.removeRestrictedItemPickup(itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
    }

}
