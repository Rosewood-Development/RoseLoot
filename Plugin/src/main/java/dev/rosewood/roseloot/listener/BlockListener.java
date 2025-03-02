package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseConfig;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.hook.CoreProtectRecentBlockHook;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.manager.SupportedBlockManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ExplosionResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockListener extends LazyLootTableListener {

    public BlockListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.BLOCK);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        RoseConfig config = this.rosePlugin.getRoseConfig();
        Block block = event.getBlock();
        if (config.get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();
        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(LootContextParams.HAS_EXISTING_ITEMS, !block.getDrops(event.getPlayer().getInventory().getItemInMainHand()).isEmpty())
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            event.setDropItems(false);

        if (lootResult.doesOverwriteExisting(OverwriteExisting.EXPERIENCE))
            event.setExpToDrop(0);

        // Drop items and experience
        Location dropLocation = block.getLocation();
        List<Item> droppedItems = new ArrayList<>();
        lootContents.getItems().forEach(x -> droppedItems.add(block.getWorld().dropItemNaturally(dropLocation, x)));

        // Simulate a BlockDropItemEvent for each item dropped for better custom enchantment plugin support if enabled
        if (!droppedItems.isEmpty() && config.get(SettingKey.SIMULATE_BLOCKDROPITEMEVENT)) {
            List<Item> eventItems = new ArrayList<>(droppedItems);
            BlockDropItemEvent blockDropItemEvent = new BlockDropItemEvent(block, block.getState(), player, eventItems);
            Bukkit.getPluginManager().callEvent(blockDropItemEvent);
            if (!blockDropItemEvent.isCancelled()) {
                droppedItems.stream().filter(x -> !eventItems.contains(x)).forEach(Entity::remove);
            } else {
                droppedItems.forEach(Entity::remove);
            }
        }

        event.setExpToDrop(event.getExpToDrop() + lootContents.getExperience());

        Block above = block.getRelative(BlockFace.UP);
        if (above.getType() == block.getType())
            this.rosePlugin.getManager(SupportedBlockManager.class).handleSupportedBlock(player, block);

        lootContents.triggerExtras(dropLocation);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceMonitor(BlockPlaceEvent event) {
        CoreProtectRecentBlockHook.markBlock(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (event instanceof LootedLeavesDecayEvent)
            return;

        RoseConfig config = this.rosePlugin.getRoseConfig();
        Block block = event.getBlock();
        if (config.get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTED_BLOCK, block)
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
            if (config.get(SettingKey.SIMULATE_LEAVESDECAYEVENT)) {
                LootedLeavesDecayEvent lootedEvent = new LootedLeavesDecayEvent(block);
                Bukkit.getPluginManager().callEvent(lootedEvent);
                if (lootedEvent.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(true);
            block.setType(Material.AIR);
        }

        // Drop items and experience
        Location dropLocation = block.getLocation();
        lootContents.getItems().forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        int experience = lootContents.getExperience();
        if (experience > 0)
            EntitySpawnUtil.spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(block.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (NMSUtil.getVersionNumber() >= 21 && !(event.getExplosionResult() == ExplosionResult.DESTROY || event.getExplosionResult() == ExplosionResult.DESTROY_WITH_DECAY))
            return;

        RoseConfig config = this.rosePlugin.getRoseConfig();
        if (!config.get(SettingKey.ALLOW_BLOCK_EXPLOSION_LOOT))
            return;

        Block block = event.getBlock();
        if (config.get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block exploded = iterator.next();

            LootContext lootContext = LootContext.builder()
                    .put(LootContextParams.ORIGIN, exploded.getLocation())
                    .put(LootContextParams.LOOTED_BLOCK, exploded)
                    .put(LootContextParams.EXPLOSION_TYPE, ExplosionType.BLOCK)
                    .put(LootContextParams.HAS_EXISTING_ITEMS, !exploded.getDrops().isEmpty())
                    .build();
            LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.BLOCK, lootContext);
            if (lootResult.isEmpty())
                continue;

            LootContents lootContents = lootResult.getLootContents();

            if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
                iterator.remove();
                exploded.setType(Material.AIR);
            }

            // Drop items and experience
            Location dropLocation = exploded.getLocation();
            lootContents.getItems().forEach(x -> exploded.getWorld().dropItemNaturally(dropLocation, x));

            int experience = lootContents.getExperience();
            if (experience > 0)
                EntitySpawnUtil.spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

            lootContents.triggerExtras(dropLocation);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (NMSUtil.getVersionNumber() >= 21 && !(event.getExplosionResult() == ExplosionResult.DESTROY || event.getExplosionResult() == ExplosionResult.DESTROY_WITH_DECAY))
            return;

        RoseConfig config = this.rosePlugin.getRoseConfig();
        if (config.get(SettingKey.ALLOW_BLOCK_EXPLOSION_LOOT))
            return;

        Entity looter = event.getEntity();
        if (config.get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(event.getEntity().getWorld().getName())))
            return;

        if (looter instanceof TNTPrimed tnt) {
            Entity source = tnt.getSource();
            if (source != null) {
                if (source instanceof Projectile projectile) {
                    if (projectile.getShooter() instanceof Player)
                        looter = (Player) projectile.getShooter();
                } else if (source.getType() == EntityType.PLAYER) {
                    looter = source;
                }
            }
        }

        ExplosionType explosionType = looter instanceof Creeper creeper && creeper.isPowered() ? ExplosionType.CHARGED_ENTITY : ExplosionType.ENTITY;
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block exploded = iterator.next();

            LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(looter))
                    .put(LootContextParams.ORIGIN, exploded.getLocation())
                    .put(LootContextParams.LOOTER, looter)
                    .put(LootContextParams.LOOTED_BLOCK, exploded)
                    .put(LootContextParams.EXPLOSION_TYPE, explosionType)
                    .put(LootContextParams.HAS_EXISTING_ITEMS, !exploded.getDrops().isEmpty())
                    .build();
            LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.BLOCK, lootContext);
            if (lootResult.isEmpty())
                continue;

            LootContents lootContents = lootResult.getLootContents();

            if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
                iterator.remove();
                exploded.setType(Material.AIR);
            }

            lootContents.dropAtLocation(exploded.getLocation());
        }
    }

    private static class LootedLeavesDecayEvent extends LeavesDecayEvent {

        public LootedLeavesDecayEvent(Block block) {
            super(block);
        }

    }

}
