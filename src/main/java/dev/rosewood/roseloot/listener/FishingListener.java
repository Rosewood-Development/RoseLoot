package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Tag;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class FishingListener implements Listener {

    private final LootTableManager lootTableManager;

    public FishingListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
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
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.FISHING, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        if (lootResult.shouldOverwriteItems()) {
            // Prevent the reel
            event.setCancelled(true);

            // Damage tool
            PlayerInventory inventory = event.getPlayer().getInventory();
            if (inventory.getItemInMainHand().getType() == Material.FISHING_ROD) {
                LootUtils.damageTool(inventory.getItemInMainHand());
            } else if (inventory.getItemInOffHand().getType() == Material.FISHING_ROD) {
                LootUtils.damageTool(inventory.getItemInOffHand());
            }

            // Remove hook
            fishHook.remove();

            // Increment statistic for each fish we caught
            lootContents.getItems()
                    .stream()
                    .filter(x -> Tag.ITEMS_FISHES.isTagged(x.getType()))
                    .forEach(x -> player.incrementStatistic(Statistic.FISH_CAUGHT));

            if (!lootResult.shouldOverwriteExperience())
                player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, x -> x.setExperience(event.getExpToDrop()));
        }

        if (lootResult.shouldOverwriteExperience())
            event.setExpToDrop(0);

        // Drop items and experience
        for (ItemStack itemStack : lootContents.getItems()) {
            double x = player.getLocation().getX() - fishHook.getLocation().getX();
            double y = player.getLocation().getY() - fishHook.getLocation().getY();
            double z = player.getLocation().getZ() - fishHook.getLocation().getZ();
            Vector motion = new Vector(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
            fishHook.getWorld().dropItem(fishHook.getLocation(), itemStack, item -> item.setVelocity(motion));
        }

        int experience = lootContents.getExperience();
        if (experience > 0)
            player.getWorld().spawn(player.getLocation(), ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(fishHook.getLocation());
    }

}
