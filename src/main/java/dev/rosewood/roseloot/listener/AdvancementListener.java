package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
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

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, player.getLocation())
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.ADVANCEMENT_KEY, event.getAdvancement().getKey())
                .build();
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.ADVANCEMENT, lootContext);
        lootResult.getLootContents().dropForPlayer(player);
    }

}
