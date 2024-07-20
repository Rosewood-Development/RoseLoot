package dev.rosewood.roseloot.listener.paper;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDropItemEvent;

public class FallingBlockListener extends LazyLootTableListener {

    public FallingBlockListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.BLOCK);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFallingBlockDropAsItem(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof FallingBlock fallingBlock))
            return;

        if (this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(fallingBlock.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, fallingBlock.getLocation())
                .put(LootContextParams.LOOTED_BLOCK_STATE, fallingBlock.getBlockState()) // Paper method, hence this being paper-only
                .put(LootContextParams.HAS_EXISTING_ITEMS, true)
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        Location dropLocation = event.getItemDrop().getLocation();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            event.setCancelled(true);

        lootContents.dropAtLocation(dropLocation);
    }

}
