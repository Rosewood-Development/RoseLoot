package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Campfire;
import org.bukkit.block.Container;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
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
            // Prevent the break
            event.setCancelled(true);

            // Damage tool
            LootUtils.damageTool(event.getPlayer().getInventory().getItemInMainHand());

            // Play block breaking effects
            block.getWorld().getNearbyEntities(block.getLocation(), 32, 32, 32, x -> x.getType() == EntityType.PLAYER)
                    .stream()
                    .map(x -> (Player) x)
                    .filter(x -> x != event.getPlayer())
                    .forEach(x -> x.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType()));

            // If the block has items in it, remove them
            BlockState blockState = block.getState();
            if (blockState instanceof Container) {
                ((Container) blockState).getSnapshotInventory().clear();
                blockState.update();
            } else if (block.getType().name().contains("CAMPFIRE")) { // No instanceof check here due to legacy version support
                Campfire campfire = (Campfire) blockState;
                for (int i = 0; i < 4; i++)
                    campfire.setItem(i, null);
                blockState.update();
            }

            // Remove the block from the world
            if (block.getBlockData() instanceof Waterlogged && ((Waterlogged) block.getBlockData()).isWaterlogged()) {
                block.setType(Material.WATER);
            } else {
                block.setType(Material.AIR);
            }

            // Increment statistic
            event.getPlayer().incrementStatistic(Statistic.MINE_BLOCK, block.getType());
        }

        // Drop items and experience
        LootContents lootContents = lootResult.getLootContents();
        Location dropLocation = block.getLocation();
        lootContents.getItems().forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        int experience = lootContents.getExperience();
        if (experience > 0)
            block.getWorld().spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

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
