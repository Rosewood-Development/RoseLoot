package dev.rosewood.roseloot.listener.hook;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.util.LootUtils;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockBreakEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;

/**
 * Implementation note: overwrite-existing is not supported for Oraxen blocks. The drops need to be
 * manually cancelled through the Oraxen configuration.
 */
public class OraxenBlockBreakListener extends LazyLootTableListener {

    public static final LootContextParam<String> ORAXEN_BLOCK = LootContextParams.create("oraxen_block", String.class);

    public OraxenBlockBreakListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.BLOCK);
    }

    @EventHandler
    public void onOraxenStringBlockBreak(OraxenStringBlockBreakEvent event) {
        this.handleOraxenBlockBreak(event.getPlayer(), event.getBlock(), event.getMechanic().getItemID());
    }

    @EventHandler
    public void onOraxenNoteBlockBreak(OraxenNoteBlockBreakEvent event) {
        this.handleOraxenBlockBreak(event.getPlayer(), event.getBlock(), event.getMechanic().getItemID());
    }

    private void handleOraxenBlockBreak(Player player, Block block, String itemId) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(ORAXEN_BLOCK, itemId)
                .build();
        LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Drop items and experience
        Location dropLocation = block.getLocation();
        List<Item> droppedItems = new ArrayList<>();
        lootContents.getItems().forEach(x -> droppedItems.add(block.getWorld().dropItemNaturally(dropLocation, x)));

        // Simulate a BlockDropItemEvent for each item dropped for better custom enchantment plugin support if enabled
        if (!droppedItems.isEmpty() && ConfigurationManager.Setting.SIMULATE_BLOCKDROPITEMEVENT.getBoolean()) {
            List<Item> eventItems = new ArrayList<>(droppedItems);
            BlockDropItemEvent blockDropItemEvent = new BlockDropItemEvent(block, block.getState(), player, eventItems);
            Bukkit.getPluginManager().callEvent(blockDropItemEvent);
            if (!blockDropItemEvent.isCancelled()) {
                droppedItems.stream().filter(x -> !eventItems.contains(x)).forEach(Entity::remove);
            } else {
                droppedItems.forEach(Entity::remove);
            }
        }

        int experience = lootContents.getExperience();
        if (experience > 0) {
            Location location = player.getLocation();
            EntitySpawnUtil.spawn(location, ExperienceOrb.class, x -> x.setExperience(experience));
        }

        lootContents.triggerExtras(dropLocation);
    }

}
