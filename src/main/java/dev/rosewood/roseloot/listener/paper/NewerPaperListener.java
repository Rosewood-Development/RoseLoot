package dev.rosewood.roseloot.listener.paper;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NewerPaperListener implements Listener {

    private final LootTableManager lootTableManager;

    public NewerPaperListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakBlockEvent(BlockBreakBlockEvent event) {
        Block block = event.getBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(LootContextParams.REPLACED_BLOCK_DATA, event.getSource().getBlockData())
                .put(LootContextParams.HAS_EXISTING_ITEMS, !event.getDrops().isEmpty())
                .build();
        LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            event.getDrops().clear();

        // Drop items and experience
        Location dropLocation = block.getLocation();
        event.getDrops().addAll(lootContents.getItems());

        int experience = lootContents.getExperience();
        if (experience > 0)
            block.getWorld().spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(dropLocation);
    }

}
