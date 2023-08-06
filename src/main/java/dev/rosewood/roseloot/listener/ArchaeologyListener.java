package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
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

        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LootTable lootTable = brushableBlock.getLootTable();
        if (lootTable == null)
            return;

        Player player = event.getPlayer();
        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.VANILLA_LOOT_TABLE_KEY, lootTable.getKey())
                .build();
        LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.ARCHAEOLOGY, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        List<ItemStack> items = new ArrayList<>(lootContents.getItems());
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
            if (items.isEmpty()) {
                brushableBlock.setItem(null);
            } else {
                brushableBlock.setItem(items.remove(0));
            }
            brushableBlock.setLootTable(null);
            brushableBlock.update();
        }

        Location dropLocation = block.getLocation();
        items.forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        int experience = lootContents.getExperience();
        if (experience > 0)
            block.getWorld().spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(dropLocation);
    }

}
