package dev.rosewood.roseloot.manager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.event.LootItemTypeRegistrationEvent;
import dev.rosewood.roseloot.event.LootTableTypeRegistrationEvent;
import dev.rosewood.roseloot.event.PostLootGenerateEvent;
import dev.rosewood.roseloot.hook.MMOCoreHook;
import dev.rosewood.roseloot.loot.LootComponent;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditionParser;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ChangeToolDurabilityLootItem;
import dev.rosewood.roseloot.loot.item.CommandLootItem;
import dev.rosewood.roseloot.loot.item.ContainerContentsLootItem;
import dev.rosewood.roseloot.loot.item.CustomItemLootItem;
import dev.rosewood.roseloot.loot.item.DiscordWebhookLootItem;
import dev.rosewood.roseloot.loot.item.EconomyLootItem;
import dev.rosewood.roseloot.loot.item.EntityEquipmentLootItem;
import dev.rosewood.roseloot.loot.item.ExperienceLootItem;
import dev.rosewood.roseloot.loot.item.ExplosionLootItem;
import dev.rosewood.roseloot.loot.item.FireworkLootItem;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.LootTableLootItem;
import dev.rosewood.roseloot.loot.item.MMOCoreExperienceLootItem;
import dev.rosewood.roseloot.loot.item.MessageLootItem;
import dev.rosewood.roseloot.loot.item.ParticleLootItem;
import dev.rosewood.roseloot.loot.item.PotionEffectLootItem;
import dev.rosewood.roseloot.loot.item.RandomNumberLootItem;
import dev.rosewood.roseloot.loot.item.SoundLootItem;
import dev.rosewood.roseloot.loot.item.TagLootItem;
import dev.rosewood.roseloot.loot.item.VoucherLootItem;
import dev.rosewood.roseloot.loot.table.LootTableType;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VanillaLootTableConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LootTableManager extends DelayedManager implements Listener {

    private static final List<String> RESERVED_COMPONENT_KEYS = List.of("type", "conditions", "rolls", "bonus-rolls", "weight", "quality", "children-strategy");

    private final BiMap<String, LootTableType> lootTableTypes;
    private final Multimap<LootTableType, LootTable> lootTables;
    private final Map<String, Function<ConfigurationSection, LootItem>> registeredLootItemFunctions;
    private final File directory;

    public LootTableManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.lootTableTypes = HashBiMap.create();
        this.lootTables = ArrayListMultimap.create();
        this.registeredLootItemFunctions = new HashMap<>();
        this.directory = new File(this.rosePlugin.getDataFolder(), "loottables");

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    protected void delayedReload() {
        LootTableTypeRegistrationEvent lootTableTypeRegistrationEvent = new LootTableTypeRegistrationEvent();
        Bukkit.getPluginManager().callEvent(lootTableTypeRegistrationEvent);
        this.lootTableTypes.putAll(lootTableTypeRegistrationEvent.getRegisteredLootTableTypes());
        RoseLoot.getInstance().getLogger().info("Registered " + this.lootTableTypes.size() + " loot table types.");

        LootItemTypeRegistrationEvent lootItemTypeRegistrationEvent = new LootItemTypeRegistrationEvent();
        Bukkit.getPluginManager().callEvent(lootItemTypeRegistrationEvent);
        this.registeredLootItemFunctions.putAll(lootItemTypeRegistrationEvent.getRegisteredLootItemsTypes());
        RoseLoot.getInstance().getLogger().info("Registered " + this.registeredLootItemFunctions.size() + " loot item types.");

        File examplesDirectory = new File(this.directory, "examples");
        if (!examplesDirectory.exists())
            examplesDirectory.mkdirs();

        // Copy README.txt file if it doesn't already exist
        File readme = new File(this.directory, "README.txt");
        if (!readme.exists())
            this.rosePlugin.saveResource("loottables/README.txt", false);

        VanillaLootTableConverter.convertVanilla(examplesDirectory);

        List<File> files = LootUtils.listFiles(this.directory, List.of("examples", "disabled"), List.of("yml"));
        for (File file : files) {
            try {
                this.loadFile(file);
            } catch (Exception e) {
                this.failToLoad(file, e.getMessage());
            }
        }

        RoseLoot.getInstance().getLogger().info("Loaded " + this.lootTables.values().size() + " loot tables.");
    }

    private void loadFile(File file) {
        ConfigurationSection configuration = CommentedFileConfiguration.loadConfiguration(file);
        LootTableType type = this.getLootTableType(configuration.getString("type"));
        if (type == null) {
            this.failToLoad(file, "Invalid type");
            return;
        }

        Set<OverwriteExisting> overwriteExisting;
        if (configuration.isString("overwrite-existing")) {
            String overwriteExistingString = configuration.getString("overwrite-existing", "none");
            overwriteExisting = switch (overwriteExistingString.toLowerCase()) {
                case "all", "true" -> OverwriteExisting.all();
                case "none", "false" -> OverwriteExisting.none();
                default -> OverwriteExisting.fromStrings(List.of(overwriteExistingString));
            };
        } else {
            overwriteExisting = OverwriteExisting.fromStrings(configuration.getStringList("overwrite-existing"));
        }

        boolean allowRecursion = configuration.getBoolean("allow-recursion", false);
        List<LootCondition> conditions = this.parseConditionsSection(file, configuration);

        // Find the first section key, if there are any more than one throw an error, if there are none throw an error
        ConfigurationSection rootComponentSection = this.findNextComponentsSection(file, configuration);
        if (rootComponentSection == null) {
            this.failToLoad(file, "No root component section");
            return;
        }

        List<LootComponent> lootComponents = this.getLootComponentsRecursively(file, rootComponentSection, rootComponentSection.getCurrentPath());
        String name = this.getLootTablePath(file);

        this.lootTables.put(type, new LootTable(name, type, conditions, lootComponents, overwriteExisting, allowRecursion));
    }

    private List<LootCondition> parseConditionsSection(File file, ConfigurationSection section) {
        List<LootCondition> conditions = new ArrayList<>();
        List<String> conditionStrings = section.getStringList("conditions");
        for (String conditionString : conditionStrings) {
            LootCondition condition = LootConditionParser.parse(conditionString);
            if (condition != null)  {
                conditions.add(condition);
            } else {
                this.issueLoading(file, "Invalid condition [" + conditionString + "]");
                conditions.add(LootCondition.ALWAYS_FALSE);
            }
        }
        return conditions;
    }

    private String getLootTablePath(File path) {
        List<String> pieces = new ArrayList<>();
        do {
            pieces.add(LootUtils.getFileName(path));
            path = path.getParentFile();
        } while (path != null && !path.equals(this.directory));

        Collections.reverse(pieces);
        StringBuilder stringBuilder = new StringBuilder();
        for (String piece : pieces) {
            if (stringBuilder.length() > 0)
                stringBuilder.append('/');
            stringBuilder.append(piece);
        }

        return stringBuilder.toString();
    }

    private ConfigurationSection findNextComponentsSection(File file, ConfigurationSection section) {
        ConfigurationSection foundSection = null;
        for (String key : section.getKeys(false)) {
            if (RESERVED_COMPONENT_KEYS.contains(key))
                continue;

            ConfigurationSection componentSection = section.getConfigurationSection(key);
            if (componentSection == null)
                continue;

            if (foundSection != null) {
                this.issueLoading(file, "Ignored unknown value [" + key + "]");
                continue;
            }

            foundSection = componentSection;
        }
        return foundSection;
    }

    private List<LootComponent> getLootComponentsRecursively(File file, ConfigurationSection componentsSection, String parents) {
        List<LootComponent> lootComponents = new ArrayList<>();
        for (String entryKey : componentsSection.getKeys(false)) {
            ConfigurationSection componentSection = componentsSection.getConfigurationSection(entryKey);
            if (componentSection == null) {
                this.issueLoading(file, "Component is not an object [parent: " + parents + ", component: " + entryKey + "]");
                continue;
            }

            List<LootCondition> entryConditions = this.parseConditionsSection(file, componentSection);

            NumberProvider weight = NumberProvider.fromSection(componentSection, "weight", null);
            NumberProvider quality = NumberProvider.fromSection(componentSection, "quality", 0);
            NumberProvider rolls = NumberProvider.fromSection(componentSection, "rolls", 1);
            NumberProvider bonusRolls = NumberProvider.fromSection(componentSection, "bonus-rolls", 0);

            ConfigurationSection itemsSection = componentSection.getConfigurationSection("items");
            List<LootItem> lootItems = new ArrayList<>();
            if (itemsSection != null) {
                for (String itemKey : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemKey);
                    if (itemSection == null) {
                        this.issueLoading(file, "Invalid item section [parent: " + parents + ", component: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    String lootItemType = itemSection.getString("type");
                    if (lootItemType == null) {
                        this.issueLoading(file, "Invalid item section, unset type [parent: " + parents + ", component: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    Function<ConfigurationSection, LootItem> lootItemFunction = this.registeredLootItemFunctions.get(lootItemType.toUpperCase());
                    if (lootItemFunction == null) {
                        this.issueLoading(file, "Invalid item section [pool: " + parents + ", component: " + entryKey + ", item: " + itemKey + ", type: " + lootItemType + "]");
                        continue;
                    }

                    LootItem lootItem = lootItemFunction.apply(itemSection);
                    if (lootItem == null) {
                        this.issueLoading(file, "Invalid item type [parent: " + parents + ", component: " + entryKey + ", item: " + itemKey + ", type: " + lootItemType + "]");
                        continue;
                    }

                    lootItems.add(lootItem);
                }
            }

            LootComponent.ChildrenStrategy childrenStrategy = LootComponent.ChildrenStrategy.fromString(componentSection.getString("children-strategy", LootComponent.ChildrenStrategy.NORMAL.name()));
            ConfigurationSection childrenSection = this.findNextComponentsSection(file, componentSection);
            List<LootComponent> childEntries = childrenSection != null ? this.getLootComponentsRecursively(file, childrenSection, parents) : null;

            lootComponents.add(new LootComponent(entryConditions, rolls, bonusRolls, weight, quality, lootItems, childrenStrategy, childEntries));
        }

        return lootComponents;
    }

    @Override
    public void disable() {
        this.lootTables.clear();
        this.registeredLootItemFunctions.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLootTableTypeRegistration(LootTableTypeRegistrationEvent event) {
        LootTableTypes.values().forEach(event::registerLootTableType);
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
        event.registerLootItem("container_contents", ContainerContentsLootItem::fromSection);
        event.registerLootItem("tag", TagLootItem::fromSection);
        event.registerLootItem("custom_item", CustomItemLootItem::fromSection);
        event.registerLootItem("voucher", VoucherLootItem::fromSection);
        event.registerLootItem("message", MessageLootItem::fromSection);
        event.registerLootItem("particle", ParticleLootItem::fromSection);
        event.registerLootItem("firework", FireworkLootItem::fromSection);
        event.registerLootItem("potion_effect", PotionEffectLootItem::fromSection);
        event.registerLootItem("change_tool_durability", ChangeToolDurabilityLootItem::fromSection);
        event.registerLootItem("random_number", RandomNumberLootItem::fromSection);
        event.registerLootItem("discord_webhook", DiscordWebhookLootItem::fromSection);

        if (MMOCoreHook.isEnabled())
            event.registerLootItem("mmocore_experience", MMOCoreExperienceLootItem::fromSection);
    }

    /**
     * Generates loot from all LootTables with the given LootTableType with the given LootContext.
     *
     * @param lootTableType The LootTableType of the LootTables to run
     * @param lootContext The LootContext to use when generating loot
     * @return A LootResult containing all generated loot
     */
    public LootResult getLoot(LootTableType lootTableType, LootContext lootContext) {
        LootContents lootContents = new LootContents(lootContext);
        Set<OverwriteExisting> overwriteExisting = OverwriteExisting.none();
        for (LootTable lootTable : this.lootTables.get(lootTableType)) {
            if (!lootTable.check(lootContext))
                continue;

            lootTable.populate(lootContext, lootContents);
            overwriteExisting.addAll(lootTable.getOverwriteExistingValues());
        }

        return this.callEvent(new LootResult(lootContext, lootContents, overwriteExisting));
    }

    /**
     * Generates loot from a LootTable with the given LootContext.
     *
     * @param lootTable The LootTable to run
     * @param lootContext The LootContext to use when generating loot
     * @return A LootResult containing all generated loot
     */
    public LootResult getLoot(LootTable lootTable, LootContext lootContext) {
        LootContents lootContents = new LootContents(lootContext);
        lootTable.populate(lootContext, lootContents);
        return this.callEvent(new LootResult(lootContext, lootContents, OverwriteExisting.none()));
    }

    /**
     * Calls the PostLootGenerateEvent for the given LootResult.
     * May modify the given LootResult or return a new one entirely.
     *
     * @param lootResult The LootResult to call the event with
     * @return The LootResult after the event has been called
     */
    private LootResult callEvent(LootResult lootResult) {
        if (!Setting.CALL_POSTLOOTGENERATEEVENT.getBoolean())
            return lootResult;

        PostLootGenerateEvent event = new PostLootGenerateEvent(lootResult);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return new LootResult(lootResult.getLootContext(), new LootContents(lootResult.getLootContext()), OverwriteExisting.none());

        if (!event.shouldDropItems())
            lootResult.getLootContents().removeItems();

        if (!event.shouldDropExperience())
            lootResult.getLootContents().removeExperience();

        if (!event.shouldTriggerExtras())
            lootResult.getLootContents().removeExtraTriggers();

        lootResult.setOverwriteExistingValues(event.getOverwriteExistingValues());

        return lootResult;
    }

    public LootTableType getLootTableType(String name) {
        if (name == null)
            return null;
        return this.lootTableTypes.get(name.toUpperCase());
    }

    public String getLootTableTypeName(LootTableType lootTableType) {
        return this.lootTableTypes.inverse().get(lootTableType);
    }

    public LootTable getLootTable(LootTableType lootTableType, String name) {
        return this.lootTables.get(lootTableType).stream()
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public LootTable getLootTable(String name) {
        return this.lootTables.values().stream()
                .filter(x -> x.getName().equals(name) || x.getName().replace(' ', '_').equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<LootTable> getLootTables() {
        return this.lootTables.values().stream()
                .sorted(Comparator.comparing(LootTable::getName))
                .toList();
    }

    public List<LootTable> getLootTables(LootTableType lootTableType) {
        return this.lootTables.get(lootTableType).stream()
                .sorted(Comparator.comparing(LootTable::getName))
                .toList();
    }

    public boolean isLootTableTypeUsed(Collection<LootTableType> lootTableTypes) {
        return lootTableTypes.stream().anyMatch(x -> !this.lootTables.get(x).isEmpty());
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
