package dev.rosewood.roseloot.manager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.api.RoseLootAPI;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.event.LootItemTypeRegistrationEvent;
import dev.rosewood.roseloot.event.LootTableTypeRegistrationEvent;
import dev.rosewood.roseloot.event.PostLootGenerateEvent;
import dev.rosewood.roseloot.hook.MMOCoreHook;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.LootComponent;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.condition.BooleanLootCondition;
import dev.rosewood.roseloot.loot.condition.EntityPropertyConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditionParser;
import dev.rosewood.roseloot.loot.condition.StringLootCondition;
import dev.rosewood.roseloot.loot.condition.tags.AdvancementCondition;
import dev.rosewood.roseloot.loot.condition.tags.BiomeCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.ChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.CooldownCondition;
import dev.rosewood.roseloot.loot.condition.tags.CustomModelDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.DeathCauseCondition;
import dev.rosewood.roseloot.loot.condition.tags.DimensionCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentChanceTableCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentCondition;
import dev.rosewood.roseloot.loot.condition.tags.EntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.FeatureCondition;
import dev.rosewood.roseloot.loot.condition.tags.GrownCropCondition;
import dev.rosewood.roseloot.loot.condition.tags.HasSaddleCondition;
import dev.rosewood.roseloot.loot.condition.tags.HumidityCondition;
import dev.rosewood.roseloot.loot.condition.tags.InFluidCondition;
import dev.rosewood.roseloot.loot.condition.tags.InputItemCondition;
import dev.rosewood.roseloot.loot.condition.tags.KilledByCondition;
import dev.rosewood.roseloot.loot.condition.tags.LegacyFeatureCondition;
import dev.rosewood.roseloot.loot.condition.tags.LightLevelCondition;
import dev.rosewood.roseloot.loot.condition.tags.LooterEntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.LuckChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.PlaceholderCondition;
import dev.rosewood.roseloot.loot.condition.tags.PotionEffectCondition;
import dev.rosewood.roseloot.loot.condition.tags.RelativeBlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.ReplacedBlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnReasonCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnerTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.TemperatureCondition;
import dev.rosewood.roseloot.loot.condition.tags.VanillaLootTableCondition;
import dev.rosewood.roseloot.loot.condition.tags.WeatherCondition;
import dev.rosewood.roseloot.loot.condition.tags.WorldCondition;
import dev.rosewood.roseloot.loot.condition.tags.paper.BiomeKeyCondition;
import dev.rosewood.roseloot.loot.condition.tags.paper.MoonPhaseCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
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
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VanillaLootTableConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Merchant;
import org.jetbrains.annotations.ApiStatus;

public class LootTableManager extends DelayedManager implements Listener {

    private static final List<String> RESERVED_COMPONENT_KEYS = List.of("type", "conditions", "rolls", "bonus-rolls", "weight", "quality", "children-strategy", "items");

    private final BiMap<String, LootTableType> lootTableTypes;
    private final Multimap<LootTableType, LootTable> lootTables;
    private final Map<String, Function<ConfigurationSection, LootItem>> registeredLootItemFunctions;
    private final Map<String, Function<String, LootCondition>> registeredConditionFunctions;
    private final File directory;

    public LootTableManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.lootTableTypes = HashBiMap.create();
        this.lootTables = ArrayListMultimap.create();
        this.registeredLootItemFunctions = new HashMap<>();
        this.registeredConditionFunctions = new LinkedHashMap<>();
        this.directory = new File(this.rosePlugin.getDataFolder(), "loottables");

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    protected void delayedReload() {
        LootConditionRegistrationEvent event = new LootConditionRegistrationEvent();
        Bukkit.getPluginManager().callEvent(event);
        this.registeredConditionFunctions.putAll(event.getRegisteredConditions());
        RoseLoot.getInstance().getLogger().info("Registered " + this.registeredConditionFunctions.size() + " loot table conditions.");

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
                this.failToLoad(file.getName(), e.getMessage());
            }
        }

        RoseLoot.getInstance().getLogger().info("Loaded " + this.lootTables.values().size() + " loot tables.");
    }

    private void loadFile(File file) {
        ConfigurationSection configuration = CommentedFileConfiguration.loadConfiguration(file);
        String path = this.getLootTablePath(file);
        LootTable loadedLootTable = this.loadConfiguration(path, file.getName(), configuration);
        if (loadedLootTable != null)
            this.lootTables.put(loadedLootTable.getType(), loadedLootTable);
    }

    @ApiStatus.Internal
    public LootTable loadConfiguration(String path, String fileName, ConfigurationSection configuration) {
        LootTableType type = this.getLootTableType(configuration.getString("type", "LOOT_TABLE"));
        if (type == null) {
            this.failToLoad(fileName, "Invalid type");
            return null;
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
        List<LootCondition> conditions = this.parseConditionsSection(fileName, configuration);

        // Find the first section key, if there are any more than one throw an error, if there are none throw an error
        ConfigurationSection rootComponentSection = this.findNextComponentsSection(fileName, configuration);
        if (rootComponentSection == null) {
            this.failToLoad(fileName, "No root component section");
            return null;
        }

        List<LootComponent> lootComponents = this.getLootComponentsRecursively(fileName, rootComponentSection, rootComponentSection.getCurrentPath());
        return new LootTable(path, type, conditions, lootComponents, overwriteExisting, allowRecursion);
    }

    private List<LootCondition> parseConditionsSection(String fileName, ConfigurationSection section) {
        List<LootCondition> conditions = new ArrayList<>();
        List<String> conditionStrings = section.getStringList("conditions");
        for (String conditionString : conditionStrings) {
            LootCondition condition = LootConditionParser.parse(conditionString);
            if (condition != null)  {
                conditions.add(condition);
            } else {
                this.issueLoading(fileName, "Invalid condition [" + conditionString + "]");
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

    private ConfigurationSection findNextComponentsSection(String fileName, ConfigurationSection section) {
        ConfigurationSection foundSection = null;
        for (String key : section.getKeys(false)) {
            if (RESERVED_COMPONENT_KEYS.contains(key))
                continue;

            ConfigurationSection componentSection = section.getConfigurationSection(key);
            if (componentSection == null)
                continue;

            if (foundSection != null) {
                this.issueLoading(fileName, "Ignored unknown value [" + key + "]");
                continue;
            }

            foundSection = componentSection;
        }
        return foundSection;
    }

    private List<LootComponent> getLootComponentsRecursively(String fileName, ConfigurationSection componentsSection, String parents) {
        List<LootComponent> lootComponents = new ArrayList<>();
        for (String entryKey : componentsSection.getKeys(false)) {
            ConfigurationSection componentSection = componentsSection.getConfigurationSection(entryKey);
            if (componentSection == null) {
                this.issueLoading(fileName, "Component is not an object [parent: " + parents + ", component: " + entryKey + "]");
                continue;
            }

            List<LootCondition> entryConditions = this.parseConditionsSection(fileName, componentSection);

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
                        this.issueLoading(fileName, "Invalid item section [parent: " + parents + ", component: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    String lootItemType = itemSection.getString("type");
                    if (lootItemType == null) {
                        this.issueLoading(fileName, "Invalid item section, unset type [parent: " + parents + ", component: " + entryKey + ", item: " + itemKey + "]");
                        continue;
                    }

                    Function<ConfigurationSection, LootItem> lootItemFunction = this.registeredLootItemFunctions.get(lootItemType.toUpperCase());
                    if (lootItemFunction == null) {
                        this.issueLoading(fileName, "Invalid item section [pool: " + parents + ", component: " + entryKey + ", item: " + itemKey + ", type: " + lootItemType + "]");
                        continue;
                    }

                    LootItem lootItem = lootItemFunction.apply(itemSection);
                    if (lootItem == null) {
                        this.issueLoading(fileName, "Invalid item type [parent: " + parents + ", component: " + entryKey + ", item: " + itemKey + ", type: " + lootItemType + "]");
                        continue;
                    }

                    lootItems.add(lootItem);
                }
            }

            LootComponent.ChildrenStrategy childrenStrategy = LootComponent.ChildrenStrategy.fromString(componentSection.getString("children-strategy", LootComponent.ChildrenStrategy.NORMAL.name()));
            ConfigurationSection childrenSection = this.findNextComponentsSection(fileName, componentSection);
            List<LootComponent> childEntries = childrenSection != null ? this.getLootComponentsRecursively(fileName, childrenSection, parents) : null;

            lootComponents.add(new LootComponent(entryConditions, rolls, bonusRolls, weight, quality, lootItems, childrenStrategy, childEntries));
        }

        return lootComponents;
    }

    @Override
    public void disable() {
        this.lootTables.clear();
        this.registeredLootItemFunctions.clear();
        this.registeredConditionFunctions.clear();
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        event.registerLootCondition("advancement", AdvancementCondition::new);
        this.registerBoolean(event, "baby", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Ageable.class).filter(x -> !x.isAdult()).isPresent());
        event.registerLootCondition("biome", BiomeCondition::new);
        event.registerLootCondition("block-data", BlockDataCondition::new);
        event.registerLootCondition("block-type", BlockTypeCondition::new);
        this.registerBoolean(event, "burning", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x.getFireTicks() > 0).isPresent());
        this.registerBoolean(event, "can-breed", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Breedable.class).filter(Breedable::canBreed).isPresent());
        this.registerBoolean(event, "can-join-raid", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x instanceof Raider raider && raider.isCanJoinRaid()).isPresent());
        event.registerLootCondition("chance", ChanceCondition::new);
        this.registerBoolean(event, "charged-explosion", context -> context.get(LootContextParams.EXPLOSION_TYPE).filter(x -> x == ExplosionType.CHARGED_ENTITY).isPresent());
        this.registerBoolean(event, "chested", context -> context.getAs(LootContextParams.LOOTED_ENTITY, ChestedHorse.class).filter(ChestedHorse::isCarryingChest).isPresent());
        event.registerLootCondition("cooldown", CooldownCondition::new);
        event.registerLootCondition("custom-model-data", CustomModelDataCondition::new);
        event.registerLootCondition("death-cause", DeathCauseCondition::new);
        event.registerLootCondition("dimension", DimensionCondition::new);
        event.registerLootCondition("enchantment-chance-table", EnchantmentChanceTableCondition::new);
        event.registerLootCondition("enchantment-chance", EnchantmentChanceCondition::new);
        event.registerLootCondition("enchantment", EnchantmentCondition::new);
        event.registerLootCondition("entity-type", EntityTypeCondition::new);
        this.registerBoolean(event, "explosion", context -> context.get(LootContextParams.EXPLOSION_TYPE).isPresent());
        if (NMSUtil.getVersionNumber() >= 19) {
            event.registerLootCondition("feature", FeatureCondition::new);
        } else {
            event.registerLootCondition("feature", LegacyFeatureCondition::new);
        }
        this.registerBoolean(event, "freezing", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x.getFreezeTicks() > 0).isPresent());
        event.registerLootCondition("grown-crop", GrownCropCondition::new);
        this.registerBoolean(event, "has-existing-drops", context -> context.get(LootContextParams.HAS_EXISTING_ITEMS).orElse(false));
        event.registerLootCondition("has-saddle", HasSaddleCondition::new);
        event.registerLootCondition("humidity", HumidityCondition::new);
        event.registerLootCondition("in-fluid", InFluidCondition::new);
        event.registerLootCondition("input-item", InputItemCondition::new);
        event.registerLootCondition("killed-by", KilledByCondition::new);
        event.registerLootCondition("light-level", LightLevelCondition::new);
        event.registerLootCondition("looter-entity-type", LooterEntityTypeCondition::new);
        event.registerLootCondition("luck-chance", LuckChanceCondition::new);
        this.registerBoolean(event, "on-ground", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(Entity::isOnGround).isPresent());
        this.registerBoolean(event, "open-water", context -> context.get(LootContextParams.FISH_HOOK).filter(FishHook::isInOpenWater).isPresent());
        this.registerBoolean(event, "patrol-leader", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Raider.class).filter(Raider::isPatrolLeader).isPresent());
        this.registerStrings(event, "permission", (context, values) -> context.get(LootContextParams.LOOTER).filter(x -> values.stream().anyMatch(x::hasPermission)).isPresent());
        event.registerLootCondition("placeholder", PlaceholderCondition::new);
        event.registerLootCondition("potion-effect", PotionEffectCondition::new);
        event.registerLootCondition("relative-block-type", RelativeBlockTypeCondition::new);
        event.registerLootCondition("required-tool", RequiredToolCondition::new);
        event.registerLootCondition("required-tool-type", RequiredToolTypeCondition::new);
        event.registerLootCondition("replaced-block-type", ReplacedBlockTypeCondition::new);
        this.registerBoolean(event, "sitting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Sittable.class).filter(Sittable::isSitting).isPresent());
        this.registerBoolean(event, "sleeping", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(LivingEntity::isSleeping).isPresent());
        this.registerBoolean(event, "sneaking", context -> context.getLootingPlayer().filter(Player::isSneaking).isPresent());
        event.registerLootCondition("spawner-type", SpawnerTypeCondition::new);
        event.registerLootCondition("spawn-reason", SpawnReasonCondition::new);
        this.registerBoolean(event, "tamed", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Tameable.class).filter(Tameable::isTamed).isPresent());
        this.registerBoolean(event, "tamed-pet-owner", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Tameable.class).map(Tameable::getOwner).flatMap(x -> context.get(LootContextParams.LOOTER).filter(y -> x.getUniqueId().equals(y.getUniqueId()))).isPresent());
        event.registerLootCondition("temperature", TemperatureCondition::new);
        this.registerBoolean(event, "trading", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Merchant.class).filter(Merchant::isTrading).isPresent());
        event.registerLootCondition("vanilla-loot-table", VanillaLootTableCondition::new);
        event.registerLootCondition("weather", WeatherCondition::new);
        event.registerLootCondition("world", WorldCondition::new);

        if (NMSUtil.isPaper()) {
            if (NMSUtil.getVersionNumber() >= 19)
                event.registerLootCondition("biome-key", BiomeKeyCondition::new);
            event.registerLootCondition("moon-phase", MoonPhaseCondition::new);
        }

        EntityPropertyConditions.apply(event, "", LootContextParams.LOOTED_ENTITY);
        EntityPropertyConditions.apply(event, "killer-", LootContextParams.LOOTER);

        RoseLootAPI.getInstance().getRegisteredCustomLootConditions().forEach(event::registerLootCondition);
    }

    private void registerBoolean(LootConditionRegistrationEvent event, String name, Predicate<LootContext> predicate) {
        event.registerLootCondition(name, tag -> new BooleanLootCondition(tag, predicate));
    }

    private void registerStrings(LootConditionRegistrationEvent event, String name, BiPredicate<LootContext, List<String>> predicate) {
        event.registerLootCondition(name, tag -> new StringLootCondition(tag, predicate));
    }

    /**
     * Parses a LootCondition tag into a registered LootCondition if one exists
     *
     * @param tag The LootCondition tag to parse
     * @return the parsed LootCondition, or null if a tag with the name does not exist or the tag was malformed
     */
    public LootCondition parseCondition(String tag) {
        int index = tag.indexOf(":");
        String tagPrefix = index == -1 ? tag : tag.substring(0, index);

        Function<String, LootCondition> factory = this.registeredConditionFunctions.get(tagPrefix);
        if (factory == null)
            return null;

        try {
            return factory.apply(tag);
        } catch (IllegalArgumentException e) {
            RoseLoot.getInstance().getLogger().warning("Failed to parse condition [" + tag + "] due to invalid values: " + e.getMessage());
        }

        return null;
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
        if (!this.rosePlugin.getRoseConfig().get(SettingKey.CALL_POSTLOOTGENERATEEVENT))
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

    private void issueLoading(String fileName, String reason) {
        this.rosePlugin.getLogger().warning("Skipped loading part of loottables/" + fileName + ": " + reason);
    }

    private void failToLoad(String fileName, String reason) {
        if (reason != null) {
            this.rosePlugin.getLogger().warning("Failed to load loottables/" + fileName + ": " + reason);
        } else {
            this.rosePlugin.getLogger().warning("Failed to load loottables/" + fileName);
        }
    }

}
