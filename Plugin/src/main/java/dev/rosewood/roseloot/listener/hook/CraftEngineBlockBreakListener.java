package dev.rosewood.roseloot.listener.hook;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseConfig;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockBreakEvent;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;

@SuppressWarnings("unchecked")
public class CraftEngineBlockBreakListener extends LazyLootTableListener  {

    public static final LootContextParam<String> CRAFTENGINE_BLOCK = LootContextParams.create("craftengine_block", String.class);
    public static final LootContextParam<List<String>> CRAFTENGINE_BLOCK_DATA = LootContextParams.create("craftengine_block_data", (Class<List<String>>) (Class<?>) List.class);

    public CraftEngineBlockBreakListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.BLOCK);
    }

    @EventHandler
    public void onCraftEngineBlockBreak(CustomBlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;

        RoseConfig config = this.rosePlugin.getRoseConfig();
        Block block = event.bukkitBlock();
        if (config.get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();
        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(CRAFTENGINE_BLOCK, event.customBlock().id().asString())
                .put(CRAFTENGINE_BLOCK_DATA, this.getBlockDataProperties(event.blockState()))
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Drop items and experience
        Location dropLocation = LootUtils.adjustBlockLocation(block.getLocation());
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

        int experience = lootContents.getExperience();
        if (experience > 0)
            EntitySpawnUtil.spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(dropLocation);
    }

    private List<String> getBlockDataProperties(ImmutableBlockState blockState) {
        return blockState.propertyEntries().entrySet().stream()
                .map(x -> x.getKey().name() + "=" + x.getValue())
                .map(String::toLowerCase)
                .toList();
    }

}
