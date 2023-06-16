package dev.rosewood.roseloot.listener;

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
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

public class HarvestBlockListener implements Listener {

    private final LootTableManager lootTableManager;

    public HarvestBlockListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockHarvest(PlayerHarvestBlockEvent event) {
        Block block = event.getHarvestedBlock();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(block.getWorld().getName())))
            return;

        Player player = event.getPlayer();

        List<ItemStack> drops = new ArrayList<>();
        int experience = 0;

        for (ItemStack itemStack : event.getItemsHarvested()) {
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

        event.getItemsHarvested().clear();
        event.getItemsHarvested().addAll(drops);

        // Drop experience
        if (experience > 0) {
            int finalExperience = experience;
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, x -> x.setExperience(finalExperience));
        }
    }

}
