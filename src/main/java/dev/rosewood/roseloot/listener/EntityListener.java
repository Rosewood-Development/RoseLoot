package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener {

    private final LootTableManager lootTableManager;

    public EntityListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(entity.getWorld().getName())))
            return;

        LivingEntity looter = null;
        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager();
            if (damager instanceof LivingEntity) {
                looter = (LivingEntity) damager;
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof LivingEntity)
                    looter = (LivingEntity) projectile.getShooter();
            }
        }

        LootContext lootContext = new LootContext(looter, entity);
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.ENTITY, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.shouldOverwriteExisting()) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        // Add items to drops and adjust experience
        event.getDrops().addAll(lootContents.getItems());
        event.setDroppedExp(event.getDroppedExp() + lootContents.getExperience());

        // Run commands
        if (!lootContents.getCommands().isEmpty()) {
            Location location = entity.getLocation();
            StringPlaceholders.Builder stringPlaceholdersBuilder = StringPlaceholders.builder("world", entity.getWorld().getName())
                    .addPlaceholder("x", location.getX())
                    .addPlaceholder("y", location.getY())
                    .addPlaceholder("z", location.getZ());

            boolean isPlayer = looter instanceof Player;
            if (isPlayer)
                stringPlaceholdersBuilder.addPlaceholder("player", looter.getName());

            StringPlaceholders stringPlaceholders = stringPlaceholdersBuilder.build();
            for (String command : lootContents.getCommands())
                if (!command.contains("%player%") || isPlayer)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stringPlaceholders.apply(command));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        // Tag all spawned entities with the spawn reason
        LootUtils.setEntitySpawnReason(event.getEntity(), event.getSpawnReason());
    }

}
