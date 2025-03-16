package dev.rosewood.roseloot.listener;

import com.google.common.collect.Iterables;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

public class ArchaeologyListener extends LazyLootTableListener {

    public ArchaeologyListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.ARCHAEOLOGY);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBrush(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Material type = block.getType();
        if (type != Material.SUSPICIOUS_SAND && type != Material.SUSPICIOUS_GRAVEL)
            return;

        if (!(block.getState() instanceof BrushableBlock brushableBlock))
            return;

        ItemStack itemUsed = event.getItem();
        if (itemUsed == null || itemUsed.getType() != Material.BRUSH)
            return;

        if (this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();
        LootContext.Builder lootContextBuilder = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(LootContextParams.LOOTER, player);

        LootTable lootTable = brushableBlock.getLootTable();
        boolean hasExistingLootTable = lootTable != null;
        if (hasExistingLootTable) {
            lootContextBuilder.put(LootContextParams.VANILLA_LOOT_TABLE_KEY, lootTable.getKey());
        } else if (brushableBlock.getItem() != null && brushableBlock.getItem().getType() != Material.AIR) {
            return; // already generated, your loot table must add an item to the brushable block otherwise it will keep generating more roseloot
        }

        LootContext lootContext = lootContextBuilder.build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.ARCHAEOLOGY, lootContext);
        LootContents lootContents = lootResult.getLootContents();
        List<ItemStack> items = new ArrayList<>(lootContents.getItems());
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
            if (items.isEmpty()) {
                brushableBlock.setItem(null);
            } else {
                brushableBlock.setItem(items.remove(0));
            }
        } else if (hasExistingLootTable) {
            // Unfortunately for us the loot table hasn't been run yet, so we have to run it ourselves, hopefully
            // this doesn't introduce any compatibility issues
            org.bukkit.loot.LootContext bukkitLootContext = new org.bukkit.loot.LootContext.Builder(block.getLocation()).lootedEntity(player).killer(player).build();
            Collection<ItemStack> vanillaItems = brushableBlock.getLootTable().populateLoot(LootUtils.RANDOM, bukkitLootContext);
            if (!vanillaItems.isEmpty()) {
                brushableBlock.setItem(Iterables.get(vanillaItems, 0));
            } else if (!items.isEmpty()) {
                brushableBlock.setItem(items.remove(0));
            }
        } else if (!items.isEmpty()) {
            brushableBlock.setItem(items.remove(0));
        }

        brushableBlock.setLootTable(null);
        brushableBlock.update();

        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);
        items.forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        int experience = lootContents.getExperience();
        if (experience > 0)
            EntitySpawnUtil.spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(dropLocation);
    }

}
