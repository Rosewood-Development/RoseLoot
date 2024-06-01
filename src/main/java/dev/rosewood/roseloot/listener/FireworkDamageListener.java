package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.item.FireworkLootItem;
import dev.rosewood.roseloot.util.VersionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FireworkDamageListener implements Listener {

    private final RosePlugin rosePlugin;

    public FireworkDamageListener(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFireworkExplode(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager.getType() == VersionUtils.FIREWORK_ROCKET && damager.hasMetadata(FireworkLootItem.DAMAGELESS_METADATA)) {
            damager.removeMetadata(FireworkLootItem.DAMAGELESS_METADATA, this.rosePlugin);
            event.setCancelled(true);
        }
    }

}
