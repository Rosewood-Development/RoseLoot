package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    private final LootTableManager lootTableManager;

    public BlockListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        Block block = event.getBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LootContext lootContext = new LootContext(event.getPlayer(), block);
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.BLOCK, lootContext);

        // Overwrite existing drops if applicable
        if (lootResult.shouldOverwriteExisting()) {
            event.setDropItems(false);
            event.setExpToDrop(0);
        }

        // Drop items and experience
        LootContents lootContents = lootResult.getLootContents();
        Location dropLocation = block.getLocation();
        lootContents.getItems().forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        event.setExpToDrop(event.getExpToDrop() + lootContents.getExperience());

        // Run commands
        if (!lootContents.getCommands().isEmpty()) {
            Location location = block.getLocation();
            StringPlaceholders stringPlaceholders = StringPlaceholders.builder("world", block.getWorld().getName())
                    .addPlaceholder("x", location.getX())
                    .addPlaceholder("y", location.getY())
                    .addPlaceholder("z", location.getZ())
                    .addPlaceholder("player", event.getPlayer().getName())
                    .build();

            for (String command : lootContents.getCommands())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stringPlaceholders.apply(command));
        }
    }

}
