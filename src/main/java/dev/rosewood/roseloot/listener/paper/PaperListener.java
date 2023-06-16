package dev.rosewood.roseloot.listener.paper;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PaperListener implements Listener {

    private final LootTableManager lootTableManager;

    public PaperListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockShear(PlayerShearBlockEvent event) {
        Block block = event.getBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();

        List<ItemStack> drops = new ArrayList<>();
        int experience = 0;

        for (ItemStack itemStack : event.getDrops()) {
            for (int i = 0; i < itemStack.getAmount(); i++) {
                LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                        .put(LootContextParams.ORIGIN, block.getLocation())
                        .put(LootContextParams.LOOTER, player)
                        .put(LootContextParams.LOOTED_BLOCK, block)
                        .put(LootContextParams.INPUT_ITEM, itemStack)
                        .put(LootContextParams.HAS_EXISTING_ITEMS, true)
                        .build();
                LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.HARVEST, lootContext);
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
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(drops);

        // Drop experience
        if (experience > 0) {
            int finalExperience = experience;
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, x -> x.setExperience(finalExperience));
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
        LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.BLOCK, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS)) {
            event.setCancelled(true);
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
            block.setBlockData(event.getNewState());
        }

        // Drop items and experience
        Location dropLocation = block.getLocation();
        lootContents.getItems().forEach(x -> block.getWorld().dropItemNaturally(dropLocation, x));

        int experience = lootContents.getExperience();
        if (experience > 0)
            block.getWorld().spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(dropLocation);
    }

}
