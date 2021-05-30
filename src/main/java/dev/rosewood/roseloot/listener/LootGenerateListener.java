package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.manager.LootTableManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

public class LootGenerateListener implements Listener {

    private final LootTableManager lootTableManager;

    public LootGenerateListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        if (!(event.getInventoryHolder() instanceof Container))
            return;

        Block block = ((Container) event.getInventoryHolder()).getBlock();
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LivingEntity looter = null;
        if (event.getEntity() instanceof LivingEntity)
            looter = (LivingEntity) event.getEntity();

        LootContext lootContext = new LootContext(looter, block, event.getLootTable().getKey());
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.CONTAINER, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing loot if applicable
        if (lootResult.shouldOverwriteExisting())
            event.getLoot().clear();

        // Trigger explosion if applicable
        if (lootContents.getExplosionState() != null)
            lootContents.getExplosionState().trigger(block.getLocation());

        // Set items and drop experience
        event.getLoot().addAll(lootResult.getLootContents().getItems());

        int experience = lootContents.getExperience();
        if (experience > 0) {
            Location location = looter == null ? block.getLocation() : looter.getLocation();
            block.getWorld().spawn(location, ExperienceOrb.class, x -> x.setExperience(experience));
        }

        // Run commands
        if (!lootContents.getCommands().isEmpty()) {
            Location location = looter.getLocation();
            StringPlaceholders.Builder stringPlaceholdersBuilder = StringPlaceholders.builder("world", looter.getWorld().getName())
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

}
