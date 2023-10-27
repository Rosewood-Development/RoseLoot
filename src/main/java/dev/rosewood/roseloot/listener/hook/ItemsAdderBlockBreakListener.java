package dev.rosewood.roseloot.listener.hook;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.util.EntitySpawnUtil;
import dev.rosewood.roseloot.util.LootUtils;
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
 * Implementation note: overwrite-existing is not supported for ItemsAdder blocks. The drops need to be
 * manually cancelled through the ItemsAdder configuration with cancel_drops.
 */
public class ItemsAdderBlockBreakListener extends LazyLootTableListener {

    public static final LootContextParam<String> ITEMSADDER_BLOCK = LootContextParams.create("itemsadder_block", String.class);

    public ItemsAdderBlockBreakListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.BLOCK);
    }

    @EventHandler
    public void onItemsAdderBlockBreak(CustomBlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        Block block = event.getBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();
        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(ITEMSADDER_BLOCK, event.getNamespacedID())
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
