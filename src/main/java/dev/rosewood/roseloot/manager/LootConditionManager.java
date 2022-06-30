package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.ClassUtils;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.condition.BooleanLootCondition;
import dev.rosewood.roseloot.loot.condition.CompoundLootCondition;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.StringLootCondition;
import dev.rosewood.roseloot.loot.condition.tags.AboveBlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.AdvancementCondition;
import dev.rosewood.roseloot.loot.condition.tags.BelowBlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.BiomeCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.ChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.CustomModelDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.DeathCauseCondition;
import dev.rosewood.roseloot.loot.condition.tags.DimensionCondition;
import dev.rosewood.roseloot.loot.condition.tags.DroppedItemCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentChanceTableCondition;
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
import dev.rosewood.roseloot.loot.condition.tags.PlaceholderCondition;
import dev.rosewood.roseloot.loot.condition.tags.PotionEffectCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnReasonCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnerTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.TemperatureCondition;
import dev.rosewood.roseloot.loot.condition.tags.VanillaLootTableCondition;
import dev.rosewood.roseloot.loot.condition.tags.WeatherCondition;
import dev.rosewood.roseloot.loot.condition.tags.WorldCondition;
import dev.rosewood.roseloot.loot.condition.tags.paper.MoonPhaseCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
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
    private final Map<String, BiPredicate<LootContext, List<String>>> registeredConditionStringPredicates;

    public LootConditionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.registeredConditionConstructors = new HashMap<>();
        this.registeredConditionPredicates = new HashMap<>();
        this.registeredConditionStringPredicates = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskLater(this.rosePlugin, () -> {
            LootConditionRegistrationEvent event = new LootConditionRegistrationEvent();
            Bukkit.getPluginManager().callEvent(event);
            this.registeredConditionConstructors.putAll(event.getRegisteredLootConditionConstructors());
            this.registeredConditionPredicates.putAll(event.getRegisteredConditionPredicates());
            this.registeredConditionStringPredicates.putAll(event.getRegisteredConditionStringPredicates());

            int numRegistered = this.registeredConditionConstructors.size() + this.registeredConditionPredicates.size() + this.registeredConditionStringPredicates.size();
            RoseLoot.getInstance().getLogger().info("Registered " + numRegistered + " loot table conditions.");
        }, 1);
    }

    @Override
    public void disable() {
        this.registeredConditionConstructors.clear();
        this.registeredConditionPredicates.clear();
        this.registeredConditionStringPredicates.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        event.registerLootCondition("above-block-type", AboveBlockTypeCondition.class);
        event.registerLootCondition("advancement", AdvancementCondition.class);
        event.registerLootCondition("baby", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Ageable.class).filter(x -> !x.isAdult()).isPresent());
        event.registerLootCondition("below-block-type", BelowBlockTypeCondition.class);
        event.registerLootCondition("biome", BiomeCondition.class);
        event.registerLootCondition("block-data", BlockDataCondition.class);
        event.registerLootCondition("block-type", BlockTypeCondition.class);
        event.registerLootCondition("burning", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x.getFireTicks() > 0).isPresent());
        event.registerLootCondition("can-breed", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Breedable.class).filter(Breedable::canBreed).isPresent());
        event.registerLootCondition("can-join-raid", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x instanceof Raider && ((Raider) x).isCanJoinRaid()).isPresent());
        event.registerLootCondition("chance", ChanceCondition.class);
        event.registerLootCondition("charged-explosion", context -> context.get(LootContextParams.EXPLOSION_TYPE).filter(x -> x == ExplosionType.CHARGED_ENTITY).isPresent());
        event.registerLootCondition("chested", context -> context.getAs(LootContextParams.LOOTED_ENTITY, ChestedHorse.class).filter(ChestedHorse::isCarryingChest).isPresent());
        event.registerLootCondition("custom-model-data", CustomModelDataCondition.class);
        event.registerLootCondition("death-cause", DeathCauseCondition.class);
        event.registerLootCondition("dimension", DimensionCondition.class);
        event.registerLootCondition("dropped-item", DroppedItemCondition.class);
        event.registerLootCondition("enchantment-chance-table", EnchantmentChanceTableCondition.class);
        event.registerLootCondition("enchantment-chance", EnchantmentChanceCondition.class);
        event.registerLootCondition("enchantment", EnchantmentCondition.class);
        event.registerLootCondition("entity-type", EntityTypeCondition.class);
        event.registerLootCondition("explosion", context -> context.get(LootContextParams.EXPLOSION_TYPE).isPresent());
        event.registerLootCondition("feature", FeatureCondition.class);
        event.registerLootCondition("freezing", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x.getFreezeTicks() > 0).isPresent());
        event.registerLootCondition("grown-crop", GrownCropCondition.class);
        event.registerLootCondition("has-existing-drops", context -> context.get(LootContextParams.HAS_EXISTING_ITEMS).orElse(false));
        event.registerLootCondition("has-saddle", HasSaddleCondition.class);
        event.registerLootCondition("humidity", HumidityCondition.class);
        event.registerLootCondition("in-fluid", InFluidCondition.class);
        event.registerLootCondition("killed-by", KilledByCondition.class);
        event.registerLootCondition("light-level", LightLevelCondition.class);
        event.registerLootCondition("looter-entity-type", LooterEntityTypeCondition.class);
        event.registerLootCondition("open-water", context -> context.get(LootContextParams.FISH_HOOK).filter(FishHook::isInOpenWater).isPresent());
        event.registerLootCondition("patrol-leader", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Raider.class).filter(Raider::isPatrolLeader).isPresent());
        event.registerLootCondition("permission", (context, values) -> context.get(LootContextParams.LOOTER).filter(x -> values.stream().anyMatch(x::hasPermission)).isPresent());
        event.registerLootCondition("placeholder", PlaceholderCondition.class);
        event.registerLootCondition("potion-effect", PotionEffectCondition.class);
        event.registerLootCondition("required-tool", RequiredToolCondition.class);
        event.registerLootCondition("required-tool-type", RequiredToolTypeCondition.class);
        event.registerLootCondition("sitting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Sittable.class).filter(Sittable::isSitting).isPresent());
        event.registerLootCondition("sleeping", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(LivingEntity::isSleeping).isPresent());
        event.registerLootCondition("spawner-type", SpawnerTypeCondition.class);
        event.registerLootCondition("spawn-reason", SpawnReasonCondition.class);
        event.registerLootCondition("tamed", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Tameable.class).filter(Tameable::isTamed).isPresent());
        event.registerLootCondition("temperature", TemperatureCondition.class);
        event.registerLootCondition("trading", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Merchant.class).filter(Merchant::isTrading).isPresent());
        event.registerLootCondition("vanilla-loot-table", VanillaLootTableCondition.class);
        event.registerLootCondition("weather", WeatherCondition.class);
        event.registerLootCondition("world", WorldCondition.class);

        if (NMSUtil.isPaper()) {
            event.registerLootCondition("moon-phase", MoonPhaseCondition.class);
        }

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

            BiPredicate<LootContext, List<String>> stringPredicate = this.registeredConditionStringPredicates.get(tagPrefix);
            if (stringPredicate != null)
                return new StringLootCondition(tag, stringPredicate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
