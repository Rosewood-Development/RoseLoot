package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.List;
import org.bukkit.Statistic;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishingListener implements Listener {

    private final LootTableManager lootTableManager;

    public FishingListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH || !(event.getCaught() instanceof Item itemCaught))
            return;

        Player player = event.getPlayer();
        FishHook fishHook = event.getHook();
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(fishHook.getWorld().getName())))
            return;

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player, true))
                .put(LootContextParams.ORIGIN, fishHook.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.FISH_HOOK, fishHook)
                .put(LootContextParams.HAS_EXISTING_ITEMS, fishHook.getHookedEntity() instanceof Item)
                .build();
        LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.FISHING, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        List<ItemStack> items = lootContents.getItems();
        if (lootResult.shouldOverwriteItems()) {
            // Manually change the item on the hook to the first item in the loot contents
            if (items.isEmpty()) {
                itemCaught.remove();
                player.decrementStatistic(Statistic.FISH_CAUGHT);
            } else {
                ItemStack itemStack = items.remove(0);
                itemCaught.setItemStack(itemStack);
            }
        }

        if (lootResult.shouldOverwriteExperience())
            event.setExpToDrop(0);

        // Drop items and experience
        if (!items.isEmpty()) {
            // Increment statistic for each extra item we caught
            player.incrementStatistic(Statistic.FISH_CAUGHT, items.size());

            double x = player.getLocation().getX() - fishHook.getLocation().getX();
            double y = player.getLocation().getY() - fishHook.getLocation().getY();
            double z = player.getLocation().getZ() - fishHook.getLocation().getZ();
            Vector motion = new Vector(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
            items.stream().map(itemStack -> fishHook.getWorld().dropItem(fishHook.getLocation(), itemStack)).forEach(item -> item.setVelocity(motion));
        }

        event.setExpToDrop(event.getExpToDrop() + lootContents.getExperience());

        lootContents.triggerExtras(fishHook.getLocation());
    }

}
