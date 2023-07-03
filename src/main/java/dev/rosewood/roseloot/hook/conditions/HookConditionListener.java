package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.hook.biomes.CustomBiomePlugin;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTBlockCondition;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTEntityCondition;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTItemCondition;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTLooterCondition;
import dev.rosewood.roseloot.hook.items.CustomItemPlugin;
import dev.rosewood.roseloot.loot.condition.StringLootCondition;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class HookConditionListener implements Listener {

    @EventHandler
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.getPlugin("MythicMobs") != null) {
            event.registerLootCondition("mythicmobs-type", MythicMobsTypeCondition::new);
            event.registerLootCondition("mythicmobs-entity", MythicMobsEntityCondition::new);
        }

        if (pluginManager.getPlugin("EcoBosses") != null)
            event.registerLootCondition("ecobosses-type", EcoBossesTypeCondition::new);

        if (pluginManager.getPlugin("RoseStacker") != null) {
            event.registerLootCondition("rosestacker-stacked-entity", RoseStackerStackedEntityCondition::new);
            event.registerLootCondition("rosestacker-primary-entity", RoseStackerPrimaryEntityCondition::new);
        }

        if (pluginManager.getPlugin("RealisticSeasons") != null) {
            event.registerLootCondition("realisticseasons-season", RealisticSeasonsSeasonCondition::new);
            event.registerLootCondition("realisticseasons-event", RealisticSeasonsEventCondition::new);
        }

        if (pluginManager.getPlugin("NBTAPI") != null) {
            event.registerLootCondition("nbt-block", NBTBlockCondition::new);
            event.registerLootCondition("nbt-entity", NBTEntityCondition::new);
            event.registerLootCondition("nbt-item", NBTItemCondition::new);
            event.registerLootCondition("nbt-looter", NBTLooterCondition::new);
        }

        if (pluginManager.getPlugin("ItemsAdder") != null) {
            event.registerLootCondition("itemsadder-block", ItemsAdderBlockCondition::new);
        }

        // Register conditions for custom item plugins
        for (CustomItemPlugin customItemPlugin : CustomItemPlugin.values())
            if (customItemPlugin.isEnabled() && customItemPlugin.supportsIdLookup())
                event.registerLootCondition(customItemPlugin.name().toLowerCase() + customItemPlugin.getConditionSuffix() + "-type", tag -> new StringLootCondition(tag, customItemPlugin.getLootConditionPredicate()));

        // Register conditions for custom biome plugins
        for (CustomBiomePlugin customBiomePlugin : CustomBiomePlugin.values())
            if (customBiomePlugin.isEnabled())
                event.registerLootCondition(customBiomePlugin.name().toLowerCase() + "-biome", tag -> new StringLootCondition(tag, customBiomePlugin.getLootConditionPredicate()));
    }

}
