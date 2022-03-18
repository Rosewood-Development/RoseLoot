package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Iterator;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();
        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(LootContextParams.HAS_EXISTING_ITEMS, !block.getDrops(event.getPlayer().getInventory().getItemInMainHand()).isEmpty())
                .build();
        LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.BLOCK, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.shouldOverwriteItems())
            event.setDropItems(false);

        if (lootResult.shouldOverwriteExperience())
            event.setExpToDrop(0);

        // Drop items and experience
        Location dropLocation = block.getLocation();
        lootContents.getItems().forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        event.setExpToDrop(event.getExpToDrop() + lootContents.getExperience());

        lootContents.triggerExtras(dropLocation);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!Setting.ALLOW_BLOCK_EXPLOSION_LOOT.getBoolean())
            return;

        Block block = event.getBlock();
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
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
            LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.BLOCK, lootContext);
            LootContents lootContents = lootResult.getLootContents();

            if (lootResult.shouldOverwriteItems())
                iterator.remove();

            // Drop items and experience
            Location dropLocation = exploded.getLocation();
            lootContents.getItems().forEach(x -> exploded.getWorld().dropItemNaturally(dropLocation, x));

            int experience = lootContents.getExperience();
            if (experience > 0)
                exploded.getWorld().spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

            lootContents.triggerExtras(dropLocation);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!Setting.ALLOW_BLOCK_EXPLOSION_LOOT.getBoolean())
            return;

        Entity looter = event.getEntity();
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(event.getEntity().getWorld().getName())))
            return;

        if (looter.getType() == EntityType.PRIMED_TNT) {
            TNTPrimed tnt = (TNTPrimed) looter;
            Entity source = tnt.getSource();
            if (source != null) {
                if (source instanceof Projectile) {
                    Projectile projectile = (Projectile) source;
                    if (projectile.getShooter() instanceof Player)
                        looter = (Player) projectile.getShooter();
                } else if (source.getType() == EntityType.PLAYER) {
                    looter = source;
                }
            }
        }

        ExplosionType explosionType = looter instanceof Creeper && ((Creeper) looter).isPowered() ? ExplosionType.CHARGED_ENTITY : ExplosionType.ENTITY;
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
            LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.BLOCK, lootContext);
            LootContents lootContents = lootResult.getLootContents();

            if (lootResult.shouldOverwriteItems())
                iterator.remove();

            // Drop items and experience
            Location dropLocation = exploded.getLocation();
            lootContents.getItems().forEach(x -> exploded.getWorld().dropItemNaturally(dropLocation, x));

            int experience = lootContents.getExperience();
            if (experience > 0)
                exploded.getWorld().spawn(exploded.getLocation(), ExperienceOrb.class, x -> x.setExperience(experience));

            lootContents.triggerExtras(dropLocation);
        }
    }

}
