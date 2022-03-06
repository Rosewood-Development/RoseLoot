package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class HookConditionListener implements Listener {

    @EventHandler
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("MythicMobs"))
            event.registerLootCondition("mythicmobs-type", MythicMobsTypeCondition.class);
    }

}
