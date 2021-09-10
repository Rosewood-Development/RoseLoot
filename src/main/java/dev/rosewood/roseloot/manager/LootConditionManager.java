package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.ClassUtils;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.BooleanLootCondition;
import dev.rosewood.roseloot.loot.condition.CompoundLootCondition;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.tags.AdvancementCondition;
import dev.rosewood.roseloot.loot.condition.tags.BiomeCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockTagCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.ChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.CustomModelDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.DeathCauseCondition;
import dev.rosewood.roseloot.loot.condition.tags.DimensionCondition;
import dev.rosewood.roseloot.loot.condition.tags.DroppedItemCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentCondition;
import dev.rosewood.roseloot.loot.condition.tags.EntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.FeatureCondition;
import dev.rosewood.roseloot.loot.condition.tags.GrownCropCondition;
import dev.rosewood.roseloot.loot.condition.tags.HasSaddleCondition;
import dev.rosewood.roseloot.loot.condition.tags.HumidityCondition;
import dev.rosewood.roseloot.loot.condition.tags.InFluidCondition;
import dev.rosewood.roseloot.loot.condition.tags.KilledByCondition;
import dev.rosewood.roseloot.loot.condition.tags.LightLevelCondition;
import dev.rosewood.roseloot.loot.condition.tags.LooterEntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.OnBlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.PermissionCondition;
import dev.rosewood.roseloot.loot.condition.tags.PlaceholderCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnReasonCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnerTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.TemperatureCondition;
import dev.rosewood.roseloot.loot.condition.tags.VanillaLootTableCondition;
import dev.rosewood.roseloot.loot.condition.tags.WeatherCondition;
import dev.rosewood.roseloot.loot.condition.tags.WorldCondition;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Merchant;

public class LootConditionManager extends Manager implements Listener {

    public static final String OR_PATTERN = "||";
    private static final String PACKAGE_PATH = "dev.rosewood.roseloot.loot.condition.tags.entity";
    private final Map<String, Constructor<? extends LootCondition>> registeredConditionConstructors;
    private final Map<String, Predicate<LootContext>> registeredConditionPredicates;

    public LootConditionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.registeredConditionConstructors = new HashMap<>();
        this.registeredConditionPredicates = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        LootConditionRegistrationEvent event = new LootConditionRegistrationEvent();
        Bukkit.getPluginManager().callEvent(event);
        this.registeredConditionConstructors.putAll(event.getRegisteredLootConditionConstructors());
        this.registeredConditionPredicates.putAll(event.getRegisteredConditionPredicates());
        RoseLoot.getInstance().getLogger().info("Registered " + (this.registeredConditionConstructors.size() + this.registeredConditionPredicates.size()) + " loot table conditions.");
    }

    @Override
    public void disable() {
        this.registeredConditionConstructors.clear();
        this.registeredConditionPredicates.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        event.registerLootCondition("advancement", AdvancementCondition.class);
        event.registerLootCondition("baby", context -> context.getLootedEntity() instanceof org.bukkit.entity.Ageable && !((org.bukkit.entity.Ageable) context.getLootedEntity()).isAdult());
        event.registerLootCondition("biome", BiomeCondition.class);
        event.registerLootCondition("block-data", BlockDataCondition.class);
        event.registerLootCondition("block-tag", BlockTagCondition.class);
        event.registerLootCondition("block-type", BlockTypeCondition.class);
        event.registerLootCondition("burning", context -> context.getLootedEntity() != null && context.getLootedEntity().getFireTicks() > 0);
        event.registerLootCondition("can-breed", context -> context.getLootedEntity() instanceof Breedable && ((Breedable) context.getLootedEntity()).canBreed());
        event.registerLootCondition("can-join-raid", context -> context.getLootedEntity() instanceof Raider && ((Raider) context.getLootedEntity()).isCanJoinRaid());
        event.registerLootCondition("chance", ChanceCondition.class);
        event.registerLootCondition("charged-explosion", context -> context.getExplosionType() == ExplosionType.CHARGED_ENTITY);
        event.registerLootCondition("chested", context -> context.getLootedEntity() instanceof ChestedHorse && ((ChestedHorse) context.getLootedEntity()).isCarryingChest());
        event.registerLootCondition("custom-model-data", CustomModelDataCondition.class);
        event.registerLootCondition("death-cause", DeathCauseCondition.class);
        event.registerLootCondition("dimension", DimensionCondition.class);
        event.registerLootCondition("dropped-item", DroppedItemCondition.class);
        event.registerLootCondition("enchantment-chance", EnchantmentChanceCondition.class);
        event.registerLootCondition("enchantment", EnchantmentCondition.class);
        event.registerLootCondition("entity-type", EntityTypeCondition.class);
        event.registerLootCondition("explosion", context -> context.getExplosionType() != null);
        event.registerLootCondition("feature", FeatureCondition.class);
        event.registerLootCondition("freezing", context -> context.getLootedEntity() != null && context.getLootedEntity().getFreezeTicks() > 0);
        event.registerLootCondition("grown-crop", GrownCropCondition.class);
        event.registerLootCondition("has-saddle", HasSaddleCondition.class);
        event.registerLootCondition("humidity", HumidityCondition.class);
        event.registerLootCondition("in-fluid", InFluidCondition.class);
        event.registerLootCondition("killed-by", KilledByCondition.class);
        event.registerLootCondition("light-level", LightLevelCondition.class);
        event.registerLootCondition("looter-entity-type", LooterEntityTypeCondition.class);
        event.registerLootCondition("on-block-type", OnBlockTypeCondition.class);
        event.registerLootCondition("open-water", context -> context.getFishHook() != null && context.getFishHook().isInOpenWater());
        event.registerLootCondition("patrol-leader", context -> context.getLootedEntity() instanceof Raider && !((Raider) context.getLootedEntity()).isPatrolLeader());
        event.registerLootCondition("permission", PermissionCondition.class);
        event.registerLootCondition("placeholder", PlaceholderCondition.class);
        event.registerLootCondition("required-tool", RequiredToolCondition.class);
        event.registerLootCondition("required-tool-type", RequiredToolTypeCondition.class);
        event.registerLootCondition("sitting", context -> context.getLootedEntity() instanceof Sittable && ((Sittable) context.getLootedEntity()).isSitting());
        event.registerLootCondition("sleeping", context -> context.getLootedEntity() != null && context.getLootedEntity().isSleeping());
        event.registerLootCondition("spawner-type", SpawnerTypeCondition.class);
        event.registerLootCondition("spawn-reason", SpawnReasonCondition.class);
        event.registerLootCondition("tamed", context -> context.getLootedEntity() instanceof Tameable && ((Tameable) context.getLootedEntity()).isTamed());
        event.registerLootCondition("temperature", TemperatureCondition.class);
        event.registerLootCondition("trading", context -> context.getLootedEntity() instanceof Merchant && ((Merchant) context.getLootedEntity()).isTrading());
        event.registerLootCondition("vanilla-loot-table", VanillaLootTableCondition.class);
        event.registerLootCondition("weather", WeatherCondition.class);
        event.registerLootCondition("world", WorldCondition.class);

        List<Class<EntityConditions>> classes = ClassUtils.getClassesOf(RoseLoot.getInstance(), PACKAGE_PATH, EntityConditions.class);
        List<String> ignoredLoading = new ArrayList<>();
        for (Class<EntityConditions> clazz : classes) {
            try {
                clazz.getConstructor(LootConditionRegistrationEvent.class).newInstance(event);
            } catch (Exception e) {
                // Log conditions that failed to load
                // This should only be caused by version incompatibilities
                String className = clazz.getSimpleName();
                ignoredLoading.add(className.substring(0, className.length() - 10));
            }
        }
        if (!ignoredLoading.isEmpty())
            RoseLoot.getInstance().getLogger().warning("Ignored loading conditions for: " + ignoredLoading);
    }

    /**
     * Parses a LootCondition tag into a LootCondition if one exists
     *
     * @param tag The LootCondition tag to parse
     * @return the parsed LootCondition, or null if a tag with the name does not exist or the tag was malformed
     */
    public LootCondition parse(String tag) {
        try {
            if (tag.contains(OR_PATTERN))
                return new CompoundLootCondition(tag);

            String parsed = (tag.startsWith("!") ? tag.substring(1) : tag).toLowerCase();
            int index = parsed.indexOf(":");
            String tagPrefix = index == -1 ? parsed : parsed.substring(0, index);

            Constructor<? extends LootCondition> constructor = this.registeredConditionConstructors.get(tagPrefix);
            if (constructor != null)
                return constructor.newInstance(tag);

            Predicate<LootContext> predicate = this.registeredConditionPredicates.get(tagPrefix);
            if (predicate != null)
                return new BooleanLootCondition(tag, predicate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
