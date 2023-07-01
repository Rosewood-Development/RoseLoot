package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.api.RoseLootAPI;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.condition.BooleanLootCondition;
import dev.rosewood.roseloot.loot.condition.CompoundLootCondition;
import dev.rosewood.roseloot.loot.condition.EntityPropertyConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
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
import dev.rosewood.roseloot.loot.condition.tags.paper.MoonPhaseCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
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

public class LootConditionManager extends Manager implements Listener {

    public static final String OR_PATTERN = "||";
    // Prefix -> (Tag -> new LootCondition instance)
    private final Map<String, Function<String, LootCondition>> registeredConditions;

    public LootConditionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.registeredConditions = new LinkedHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskLater(this.rosePlugin, () -> {
            LootConditionRegistrationEvent event = new LootConditionRegistrationEvent();
            Bukkit.getPluginManager().callEvent(event);
            this.registeredConditions.putAll(event.getRegisteredConditions());
            RoseLoot.getInstance().getLogger().info("Registered " + this.registeredConditions.size() + " loot table conditions.");
        }, 1);
    }

    @Override
    public void disable() {
        this.registeredConditions.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLootConditionRegistration(LootConditionRegistrationEvent event) {
        event.registerLootCondition("advancement", AdvancementCondition::new);
        this.registerBoolean(event, "baby", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Ageable.class).filter(x -> !x.isAdult()).isPresent());
        event.registerLootCondition("biome", BiomeCondition::new);
        event.registerLootCondition("block-data", BlockDataCondition::new);
        event.registerLootCondition("block-type", BlockTypeCondition::new);
        this.registerBoolean(event, "can-breed", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Breedable.class).filter(Breedable::canBreed).isPresent());
        this.registerBoolean(event, "can-join-raid", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x instanceof Raider raider && raider.isCanJoinRaid()).isPresent());
        event.registerLootCondition("chance", ChanceCondition::new);
        this.registerBoolean(event, "charged-explosion", context -> context.get(LootContextParams.EXPLOSION_TYPE).filter(x -> x == ExplosionType.CHARGED_ENTITY).isPresent());
        this.registerBoolean(event, "chested", context -> context.getAs(LootContextParams.LOOTED_ENTITY, ChestedHorse.class).filter(ChestedHorse::isCarryingChest).isPresent());
        event.registerLootCondition("cooldown", CooldownCondition::new);
        event.registerLootCondition("custom-model-data", CustomModelDataCondition::new);
        event.registerLootCondition("death-cause", DeathCauseCondition::new);
        event.registerLootCondition("dimension", DimensionCondition::new);
        event.registerLootCondition("dropped-item", DroppedItemCondition::new);
        event.registerLootCondition("enchantment-chance-table", EnchantmentChanceTableCondition::new);
        event.registerLootCondition("enchantment-chance", EnchantmentChanceCondition::new);
        event.registerLootCondition("enchantment", EnchantmentCondition::new);
        event.registerLootCondition("entity-type", EntityTypeCondition::new);
        this.registerBoolean(event, "explosion", context -> context.get(LootContextParams.EXPLOSION_TYPE).isPresent());
        event.registerLootCondition("feature", FeatureCondition::new);
        this.registerBoolean(event, "freezing", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(x -> x.getFreezeTicks() > 0).isPresent());
        event.registerLootCondition("grown-crop", GrownCropCondition::new);
        this.registerBoolean(event, "has-existing-drops", context -> context.get(LootContextParams.HAS_EXISTING_ITEMS).orElse(false));
        event.registerLootCondition("has-saddle", HasSaddleCondition::new);
        event.registerLootCondition("humidity", HumidityCondition::new);
        event.registerLootCondition("in-fluid", InFluidCondition::new);
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
        this.registerBoolean(event, "sneaking", context -> context.getLootingPlayer().map(Player::isSneaking).orElse(false));
        this.registerBoolean(event, "sitting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Sittable.class).filter(Sittable::isSitting).isPresent());
        this.registerBoolean(event, "sleeping", context -> context.get(LootContextParams.LOOTED_ENTITY).filter(LivingEntity::isSleeping).isPresent());
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
     * Parses a LootCondition tag into a LootCondition if one exists
     *
     * @param tag The LootCondition tag to parse
     * @return the parsed LootCondition, or null if a tag with the name does not exist or the tag was malformed
     */
    public LootCondition parse(String tag) {
        if (tag.contains(OR_PATTERN))
            return new CompoundLootCondition(tag);

        String parsed = (tag.startsWith("!") ? tag.substring(1) : tag).toLowerCase();
        int index = parsed.indexOf(":");
        String tagPrefix = index == -1 ? parsed : parsed.substring(0, index);

        Function<String, LootCondition> factory = this.registeredConditions.get(tagPrefix);
        if (factory == null)
            return null;

        try {
            return factory.apply(tag);
        } catch (IllegalArgumentException e) {
            RoseLoot.getInstance().getLogger().warning("Failed to parse condition [" + tag + "] due to invalid values: " + e.getMessage());
        }

        return null;
    }

}
