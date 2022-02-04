package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.event.LootItemTypeRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootEntry;
import dev.rosewood.roseloot.loot.LootPool;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.item.CommandLootItem;
import dev.rosewood.roseloot.loot.item.EconomyLootItem;
import dev.rosewood.roseloot.loot.item.EcoLootItem;
import dev.rosewood.roseloot.loot.item.EntityEquipmentLootItem;
import dev.rosewood.roseloot.loot.item.ExperienceLootItem;
import dev.rosewood.roseloot.loot.item.ExplosionLootItem;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.LootTableLootItem;
import dev.rosewood.roseloot.loot.item.SoundLootItem;
import dev.rosewood.roseloot.loot.item.TagLootItem;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import dev.rosewood.roseloot.util.VanillaLootTableConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LootTableManager extends Manager implements Listener {

    private final Map<LootTableType, List<LootTable>> lootTables;
    private final Map<String, Function<ConfigurationSection, LootItem<?>>> registeredLootItemFunctions;

    public LootTableManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.lootTables = new HashMap<>();
        this.registeredLootItemFunctions = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskLater(this.rosePlugin, () -> {
            LootConditionManager lootConditionManager = this.rosePlugin.getManager(LootConditionManager.class);

            LootItemTypeRegistrationEvent event = new LootItemTypeRegistrationEvent();
            Bukkit.getPluginManager().callEvent(event);
            this.registeredLootItemFunctions.putAll(event.getRegisteredLootItemsTypes());
            RoseLoot.getInstance().getLogger().info("Registered " + this.registeredLootItemFunctions.size() + " loot item types.");

            File directory = new File(this.rosePlugin.getDataFolder(), "loottables");
            File examplesDirectory = new File(directory, "examples");
            if (!examplesDirectory.exists()) {
                examplesDirectory.mkdirs();
                // TODO: Create additional example files
            }

            // Copy README.txt file if it doesn't already exist
            File readme = new File(directory, "README.txt");
            if (!readme.exists())
                this.rosePlugin.saveResource("loottables/README.txt", false);

            VanillaLootTableConverter.convert(examplesDirectory);

            // Populate with lists for LootTableTypes
            for (LootTableType type : LootTableType.values())
                this.lootTables.put(type, new ArrayList<>());

            List<File> files = LootUtils.listFiles(directory, Collections.singletonList("examples"), Collections.singletonList("yml"));
            for (File file : files) {
                try {
                    ConfigurationSection configuration = CommentedFileConfiguration.loadConfiguration(file);
                    LootTableType type = LootTableType.fromString(configuration.getString("type"));
                    if (type == null) {
                        this.failToLoad(file, "Invalid type");
                        continue;
                    }

                    String overwriteExistingString = configuration.getString("overwrite-existing", "none");
                    OverwriteExisting overwriteExisting = OverwriteExisting.fromString(overwriteExistingString);

                    List<LootCondition> conditions = new ArrayList<>();
                    List<String> conditionStrings = configuration.getStringList("conditions");
                    for (String conditionString : conditionStrings) {
                        LootCondition condition = lootConditionManager.parse(conditionString);
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
                            LootCondition condition = lootConditionManager.parse(conditionString);
                            if (condition == null)  {
                                this.issueLoading(file, "Invalid pool condition [" + conditionString + "]");
                                continue;
                            }
                            poolConditions.add(condition);
                        }

                        NumberProvider rolls = NumberProvider.fromSection(poolSection, "rolls", 1);
                        NumberProvider bonusRolls = NumberProvider.fromSection(poolSection, "bonus-rolls", 0);

                        ConfigurationSection entriesSection = poolSection.getConfigurationSection("entries");
                        if (entriesSection == null) {
                            this.issueLoading(file, "Missing entries section for pool [" + poolKey + "]");
                            continue;
                        }

                        List<LootEntry> entries = this.getEntriesRecursively(file, entriesSection, lootConditionManager, poolKey, false);

                        lootPools.add(new LootPool(poolConditions, rolls, bonusRolls, entries));
                    }

                    File path = file;
                    List<String> pieces = new ArrayList<>();
                    do {
                        pieces.add(LootUtils.getFileName(path));
                        path = path.getParentFile();
                    } while (path != null && !path.equals(directory));

                    Collections.reverse(pieces);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String piece : pieces) {
                        if (stringBuilder.length() > 0)
                            stringBuilder.append('/');
                        stringBuilder.append(piece);
                    }

                    this.lootTables.get(type).add(new LootTable(stringBuilder.toString(), type, conditions, lootPools, overwriteExisting));
                } catch (Exception e) {
                    this.failToLoad(file, null);
                }
            }

            RoseLoot.getInstance().getLogger().info("Loaded " + this.lootTables.values().stream().mapToInt(List::size).sum() + " loot tables.");
        }, 1);
    }

    private List<LootEntry> getEntriesRecursively(File file, ConfigurationSection entriesSection, LootConditionManager lootConditionManager, String poolKey, boolean child) {
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
                LootCondition condition = lootConditionManager.parse(conditionString);
                if (condition == null)  {
                    this.issueLoading(file, "Invalid entry condition [" + conditionString + "]");
                    continue;
                }
                entryConditions.add(condition);
            }

            NumberProvider weight;
            if (entriesSection.contains("weight")) {
                weight = NumberProvider.fromSection(entrySection, "weight", 0);
            } else {
                weight = null;
            }

            NumberProvider quality = NumberProvider.fromSection(entrySection, "quality", 0);

            ConfigurationSection itemsSection = entrySection.getConfigurationSection("items");
            List<LootItem<?>> lootItems = new ArrayList<>();
            if (itemsSection != null) {
                for (String itemKey : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemKey);
                    if (itemSection == null) {
                        this.issueLoading(file, "Invalid item section for pool/entry [pool: " + poolKey + ", entry: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    String lootItemType = itemSection.getString("type");
                    if (lootItemType == null) {
                        this.issueLoading(file, "Invalid item section for pool/entry [pool: " + poolKey + ", entry: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    Function<ConfigurationSection, LootItem<?>> lootItemFunction = this.registeredLootItemFunctions.get(lootItemType.toUpperCase());
                    if (lootItemFunction == null) {
                        this.issueLoading(file, "Invalid item for pool/entry [pool: " + poolKey + ", entry: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    LootItem<?> lootItem = lootItemFunction.apply(itemSection);
                    if (lootItem == null) {
                        this.issueLoading(file, "Invalid item for pool/entry [pool: " + poolKey + ", entry: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    lootItems.add(lootItem);
                }
            }

            LootEntry.ChildrenStrategy childrenStrategy = LootEntry.ChildrenStrategy.fromString(entrySection.getString("children-strategy", LootEntry.ChildrenStrategy.NORMAL.name()));
            ConfigurationSection childrenSection = entrySection.getConfigurationSection("children");
            List<LootEntry> childEntries = childrenSection != null ? this.getEntriesRecursively(file, childrenSection, lootConditionManager, poolKey, true) : null;

            lootEntries.add(new LootEntry(child, entryConditions, weight, quality, lootItems, childrenStrategy, childEntries));
        }

        return lootEntries;
    }

    @Override
    public void disable() {
        this.lootTables.clear();
        this.registeredLootItemFunctions.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLootItemTypeRegistration(LootItemTypeRegistrationEvent event) {
        event.registerLootItem("item", ItemLootItem::fromSection);
        event.registerLootItem("experience", ExperienceLootItem::fromSection);
        event.registerLootItem("command", CommandLootItem::fromSection);
        event.registerLootItem("loot_table", LootTableLootItem::fromSection);
        event.registerLootItem("explosion", ExplosionLootItem::fromSection);
        event.registerLootItem("sound", SoundLootItem::fromSection);
        event.registerLootItem("economy", EconomyLootItem::fromSection);
        event.registerLootItem("entity_equipment", EntityEquipmentLootItem::fromSection);
        event.registerLootItem("tag", TagLootItem::fromSection);
        event.registerLootItem("eco", EcoLootItem::fromSection);
    }

    public LootResult getLoot(LootTableType lootTableType, LootContext lootContext) {
        LootContents lootContents = new LootContents(lootContext);
        OverwriteExisting overwriteExisting = OverwriteExisting.NONE;
        for (LootTable lootTable : this.lootTables.get(lootTableType)) {
            lootContents.add(lootTable.generate(lootContext));
            overwriteExisting = OverwriteExisting.combine(overwriteExisting, lootTable.getOverwriteExistingValue(lootContext));
        }
        return new LootResult(lootContext, lootContents, overwriteExisting);
    }

    public LootTable getLootTable(LootTableType lootTableType, String name) {
        return this.lootTables.get(lootTableType).stream()
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public LootTable getLootTable(String name) {
        return this.lootTables.values().stream()
                .flatMap(Collection::stream)
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<LootTable> getLootTables() {
        return this.lootTables.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(LootTable::getName))
                .collect(Collectors.toList());
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
