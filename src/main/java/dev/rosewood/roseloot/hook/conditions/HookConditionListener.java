package dev.rosewood.roseloot.hook.conditions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.hook.biomes.CustomBiomePlugin;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTBlockCondition;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTEntityCondition;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTItemCondition;
import dev.rosewood.roseloot.hook.conditions.nbt.NBTLooterCondition;
import dev.rosewood.roseloot.hook.items.CustomItemPlugin;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class HookConditionListener implements Listener {

    private static final Multimap<String, ConditionStorage> LOOT_CONDITIONS = ArrayListMultimap.create();
    static {
        LOOT_CONDITIONS.put("MythicMobs", new ConditionStorage("mythicmobs-type", MythicMobsTypeCondition.class));
        LOOT_CONDITIONS.put("EcoBosses", new ConditionStorage("ecobosses-type", EcoBossesTypeCondition.class));
        LOOT_CONDITIONS.put("RoseStacker", new ConditionStorage("rosestacker-stacked-entity", RoseStackerStackedEntityCondition.class));
        LOOT_CONDITIONS.put("RealisticSeasons", new ConditionStorage("realisticseasons-season", RealisticSeasonsSeasonCondition.class));
        LOOT_CONDITIONS.put("RealisticSeasons", new ConditionStorage("realisticseasons-event", RealisticSeasonsEventCondition.class));
        LOOT_CONDITIONS.put("NBTAPI", new ConditionStorage("nbt-block", NBTBlockCondition.class));
        LOOT_CONDITIONS.put("NBTAPI", new ConditionStorage("nbt-entity", NBTEntityCondition.class));
        LOOT_CONDITIONS.put("NBTAPI", new ConditionStorage("nbt-item", NBTItemCondition.class));
        LOOT_CONDITIONS.put("NBTAPI", new ConditionStorage("nbt-looter", NBTLooterCondition.class));
    }

    @EventHandler
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        for (String pluginName : LOOT_CONDITIONS.keys())
            if (pluginManager.isPluginEnabled(pluginName))
                for (ConditionStorage conditionStorage : LOOT_CONDITIONS.get(pluginName))
                    event.registerLootCondition(conditionStorage.getConditionName(), conditionStorage.getConditionClass());

        // Register conditions for custom item plugins
        for (CustomItemPlugin customItemPlugin : CustomItemPlugin.values())
            if (customItemPlugin.isEnabled() && customItemPlugin.supportsIdLookup())
                event.registerLootCondition(customItemPlugin.name().toLowerCase() + "-type", customItemPlugin.getLootConditionPredicate());

        // Register conditions for custom biome plugins
        for (CustomBiomePlugin customBiomePlugin : CustomBiomePlugin.values())
            if (customBiomePlugin.isEnabled())
                event.registerLootCondition(customBiomePlugin.name().toLowerCase() + "-biome", customBiomePlugin.getLootConditionPredicate());
    }

    private static class ConditionStorage {

        private final String conditionName;
        private final Class<? extends LootCondition> conditionClass;

        private ConditionStorage(String conditionName, Class<? extends LootCondition> conditionClass) {
            this.conditionName = conditionName;
            this.conditionClass = conditionClass;
        }

        /**
         * @return The name of the condition
         */
        public String getConditionName() {
            return this.conditionName;
        }

        /**
         * @return The class of the condition
         */
        public Class<? extends LootCondition> getConditionClass() {
            return this.conditionClass;
        }

    }

}
