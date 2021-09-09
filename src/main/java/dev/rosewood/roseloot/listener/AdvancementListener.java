package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

    private final LootTableManager lootTableManager;

    public AdvancementListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(player.getWorld().getName())))
            return;

        LootContext lootContext = new LootContext(player, event.getAdvancement().getKey());
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.ADVANCEMENT, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        // Drop items and experience
        lootResult.getLootContents().getItems().forEach(x -> player.getWorld().dropItemNaturally(player.getLocation(), x));

        int experience = lootContents.getExperience();
        if (experience > 0) {
            Location location = player.getLocation();
            player.getWorld().spawn(location, ExperienceOrb.class, x -> x.setExperience(experience));
        }

        lootContents.triggerExtras(player.getLocation());
    }

}
