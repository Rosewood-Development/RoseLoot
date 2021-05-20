package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootEntry;
import dev.rosewood.roseloot.loot.LootPool;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import dev.rosewood.roseloot.loot.item.LootItem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class LootTableManager extends Manager {

    private final Map<LootTableType, List<LootTable>> lootTables;

    public LootTableManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.lootTables = new HashMap<>();
    }

    @Override
    public void reload() {
        File directory = new File(this.rosePlugin.getDataFolder(), "loottables");
        if (!directory.exists()) {
            directory.mkdirs();
            // TODO: Create example files
        }

        // Populate with lists for LootTableTypes
        for (LootTableType type : LootTableType.values())
            this.lootTables.put(type, new ArrayList<>());

        File[] files = directory.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            try {
                ConfigurationSection configuration = CommentedFileConfiguration.loadConfiguration(file);
                LootTableType type = LootTableType.fromString(configuration.getString("type"));
                if (type == null) {
                    this.failToLoad(file, "Invalid type");
                    continue;
                }

                boolean overwriteExisting = configuration.getBoolean("overwrite-existing", false);

                List<LootCondition> conditions = new ArrayList<>();
                List<String> conditionStrings = configuration.getStringList("conditions");
                for (String conditionString : conditionStrings) {
                    LootCondition condition = LootConditions.parse(conditionString);
                    if (condition == null)  {
                        this.issueLoading(file, "Invalid condition [" + conditionString + "]");
                        continue;
                    }
                    conditions.add(condition);
                }

                ConfigurationSection poolsSection = configuration.getConfigurationSection("pools");
                if (poolsSection == null) {
                    this.failToLoad(file, "No pools section");
                    continue;
                }

                List<LootPool> lootPools = new ArrayList<>();
                for (String poolKey : poolsSection.getKeys(false)) {
                    ConfigurationSection poolSection = poolsSection.getConfigurationSection(poolKey);
                    if (poolSection == null) {
                        this.issueLoading(file, "Invalid pool section [" + poolKey + "]");
                        continue;
                    }

                    List<LootCondition> poolConditions = new ArrayList<>();
                    List<String> poolConditionStrings = poolSection.getStringList("conditions");
                    for (String conditionString : poolConditionStrings) {
                        LootCondition condition = LootConditions.parse(conditionString);
                        if (condition == null)  {
                            this.issueLoading(file, "Invalid pool condition [" + conditionString + "]");
                            continue;
                        }
                        poolConditions.add(condition);
                    }

                    int rolls = poolSection.getInt("rolls", 1);
                    int bonusRolls = poolSection.getInt("bonus-rolls", 0);

                    ConfigurationSection entriesSection = poolSection.getConfigurationSection("entries");
                    if (entriesSection == null) {
                        this.issueLoading(file, "Missing entries section for pool [" + poolKey + "]");
                        continue;
                    }

                    List<LootEntry> lootEntries = new ArrayList<>();
                    for (String entryKey : entriesSection.getKeys(false)) {
                        ConfigurationSection entrySection = entriesSection.getConfigurationSection(entryKey);
                        if (entrySection == null) {
                            this.issueLoading(file, "Invalid entry section for pool [pool: " + poolKey + ", entry: " + entryKey + "]");
                            continue;
                        }

                        List<LootCondition> entryConditions = new ArrayList<>();
                        List<String> entryConditionStrings = entrySection.getStringList("conditions");
                        for (String conditionString : entryConditionStrings) {
                            LootCondition condition = LootConditions.parse(conditionString);
                            if (condition == null)  {
                                this.issueLoading(file, "Invalid entry condition [" + conditionString + "]");
                                continue;
                            }
                            entryConditions.add(condition);
                        }

                        int weight = entrySection.getInt("weight", 0);
                        int quality = entrySection.getInt("quality", 0);

                        ConfigurationSection itemsSection = entrySection.getConfigurationSection("items");
                        if (itemsSection == null) {
                            this.issueLoading(file, "Missing items section for pool/entry [" + poolKey + "/" + entryKey + "]");
                            continue;
                        }

                        List<LootItem> lootItems = new ArrayList<>();
                        for (String itemKey : itemsSection.getKeys(false)) {
                            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemKey);
                            if (itemSection == null) {
                                this.issueLoading(file, "Invalid item section for pool/entry [pool: " + poolKey + ", entry: " + entryKey + ", item: " + itemKey + "]");
                                continue;
                            }

                            LootItem lootItem = LootItem.fromSection(itemSection);
                            if (lootItem == null) {
                                this.issueLoading(file, "Invalid item for pool/entry [pool: " + poolKey + ", entry: " + entryKey + ", item: " + itemKey + "]");
                                continue;
                            }

                            lootItems.add(lootItem);
                        }

                        lootEntries.add(new LootEntry(entryConditions, weight, quality, lootItems));
                    }

                    lootPools.add(new LootPool(poolConditions, rolls, bonusRolls, lootEntries));
                }

                this.lootTables.get(type).add(new LootTable(type, conditions, lootPools, overwriteExisting));
            } catch (Exception e) {
                this.failToLoad(file, null);
            }
        }
    }

    @Override
    public void disable() {
        this.lootTables.clear();
    }

    public LootResult getLoot(LootTableType lootTableType, LootContext lootContext) {
        List<LootContents> lootContents = new ArrayList<>();
        boolean overwriteExisting = false;
        for (LootTable lootTable : this.lootTables.get(lootTableType)) {
            lootContents.add(lootTable.generate(lootContext));
            overwriteExisting |= lootTable.shouldOverwriteExisting(lootContext);
        }
        return new LootResult(lootContext, new LootContents(lootContents), overwriteExisting);
    }

    private void issueLoading(File file, String reason) {
        this.rosePlugin.getLogger().warning("Skipped loading part of loottables/" + file.getName() + ": " + reason);
    }

    private void failToLoad(File file, String reason) {
        if (reason != null) {
            this.rosePlugin.getLogger().warning("Failed to load loottables/" + file.getName() + ": " + reason);
        } else {
            this.rosePlugin.getLogger().warning("Failed to load loottables/" + file.getName());
        }
    }

}