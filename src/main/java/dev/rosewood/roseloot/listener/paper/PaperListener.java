package dev.rosewood.roseloot.listener.paper;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.util.LootUtils;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class PaperListener extends LazyLootTableListener {

    public PaperListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.HARVEST, LootTableTypes.BLOCK);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockShear(PlayerShearBlockEvent event) {
        Block block = event.getBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();

        List<ItemStack> drops = new ArrayList<>();
        int experience = 0;

        boolean any = false;
        for (ItemStack itemStack : event.getDrops()) {
            for (int i = 0; i < itemStack.getAmount(); i++) {
                LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                        .put(LootContextParams.ORIGIN, block.getLocation())
                        .put(LootContextParams.LOOTER, player)
                        .put(LootContextParams.LOOTED_BLOCK, block)
                        .put(LootContextParams.INPUT_ITEM, itemStack)
                        .put(LootContextParams.HAS_EXISTING_ITEMS, true)
                        .build();
                LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.HARVEST, lootContext);
                if (lootResult.isEmpty())
                    continue;

                LootContents lootContents = lootResult.getLootContents();

                if (!lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
                    ItemStack clone = itemStack.clone();
                    clone.setAmount(1);
                    drops.add(clone);
                }

                drops.addAll(lootContents.getItems());
                experience += lootContents.getExperience();
                any = true;
            }
        }

        if (!any)
            return;

        event.getDrops().clear();
        event.getDrops().addAll(drops);

        // Drop experience
        if (experience > 0) {
            int finalExperience = experience;
            EntitySpawnUtil.spawn(player.getLocation(), ExperienceOrb.class, x -> x.setExperience(finalExperience));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(LootContextParams.REPLACED_BLOCK_DATA, event.getNewState())
                .put(LootContextParams.HAS_EXISTING_ITEMS, !block.getDrops().isEmpty())
                .build();
        LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
            if (NMSUtil.getVersionNumber() >= 19) {
                event.setWillDrop(false);
            } else {
                event.setCancelled(true);
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                block.setBlockData(event.getNewState());
            }
        }

        // Due to the way Paper's BlockDestroyEvent is implemented, we need to delay the item drops by a tick
        // otherwise they will get destroyed immediately by the BlockBreakEvent's item cancellation
        Bukkit.getScheduler().runTask(this.rosePlugin, () -> lootContents.dropAtLocation(block.getLocation()));
    }

}
