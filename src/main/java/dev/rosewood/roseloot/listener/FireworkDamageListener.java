package dev.rosewood.roseloot.listener;

import dev.rosewood.roseloot.loot.item.FireworkLootItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FireworkDamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFireworkExplode(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager.getType() == EntityType.FIREWORK && damager.hasMetadata(FireworkLootItem.DAMAGELESS_METADATA))
            event.setCancelled(true);
    }

}
