package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.rosegarden.utils.ClassUtils;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.tags.BiomeCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockTagCondition;
import dev.rosewood.roseloot.loot.condition.tags.BlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.ChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.CustomModelDataCondition;
import dev.rosewood.roseloot.loot.condition.tags.DeathCauseCondition;
import dev.rosewood.roseloot.loot.condition.tags.DroppedItemCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.EnchantmentCondition;
import dev.rosewood.roseloot.loot.condition.tags.EntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.HasSaddleCondition;
import dev.rosewood.roseloot.loot.condition.tags.KilledByCondition;
import dev.rosewood.roseloot.loot.condition.tags.LooterEntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.PermissionCondition;
import dev.rosewood.roseloot.loot.condition.tags.PlaceholderCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolCondition;
import dev.rosewood.roseloot.loot.condition.tags.RequiredToolTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnReasonCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnerTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.VanillaLootTableCondition;
import dev.rosewood.roseloot.loot.condition.tags.WorldCondition;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.Merchant;

public class LootConditions {

    private static final String PACKAGE_PATH = "dev.rosewood.roseloot.loot.condition.tags.entity";
    private static final Map<String, Constructor<? extends LootCondition>> tagConstructorMap = new HashMap<>();
    private static final Map<String, Predicate<LootContext>> tagPredicateMap = new HashMap<>();

    static {
        registerTag("baby", context -> context.getLootedEntity() instanceof org.bukkit.entity.Ageable && !((org.bukkit.entity.Ageable) context.getLootedEntity()).isAdult());
        registerTag("biome", BiomeCondition.class);
        registerTag("block-data", BlockDataCondition.class);
        registerTag("block-tag", BlockTagCondition.class);
        registerTag("block-type", BlockTypeCondition.class);
        registerTag("burning", context -> context.getLootedEntity() != null && context.getLootedEntity().getFireTicks() > 0);
        registerTag("can-breed", context -> context.getLootedEntity() instanceof Breedable && ((Breedable) context.getLootedEntity()).canBreed());
        registerTag("can-join-raid", context -> context.getLootedEntity() instanceof Raider && ((Raider) context.getLootedEntity()).isCanJoinRaid());
        registerTag("chance", ChanceCondition.class);
        registerTag("charged-explosion", context -> context.getExplosionType() == ExplosionType.CHARGED_ENTITY);
        registerTag("chested", context -> context.getLootedEntity() instanceof ChestedHorse && ((ChestedHorse) context.getLootedEntity()).isCarryingChest());
        registerTag("custom-model-data", CustomModelDataCondition.class);
        registerTag("death-cause", DeathCauseCondition.class);
        registerTag("dropped-item", DroppedItemCondition.class);
        registerTag("enchantment-chance", EnchantmentChanceCondition.class);
        registerTag("enchantment", EnchantmentCondition.class);
        registerTag("entity-type", EntityTypeCondition.class);
        registerTag("explosion", context -> context.getExplosionType() != null);
        registerTag("freezing", context -> context.getLootedEntity() != null && context.getLootedEntity().getFreezeTicks() > 0);
        registerTag("grown-crop", context -> context.getLootedBlock() instanceof Ageable && ((Ageable) context.getLootedBlock()).getAge() == ((Ageable) context.getLootedBlock()).getMaximumAge());
        registerTag("has-saddle", HasSaddleCondition.class);
        registerTag("killed-by", KilledByCondition.class);
        registerTag("looter-entity-type", LooterEntityTypeCondition.class);
        registerTag("open-water", context -> context.getFishHook() != null && context.getFishHook().isInOpenWater());
        registerTag("patrol-leader", context -> context.getLootedEntity() instanceof Raider && !((Raider) context.getLootedEntity()).isPatrolLeader());
        registerTag("permission", PermissionCondition.class);
        registerTag("placeholder", PlaceholderCondition.class);
        registerTag("required-tool", RequiredToolCondition.class);
        registerTag("required-tool-type", RequiredToolTypeCondition.class);
        registerTag("sitting", context -> context.getLootedEntity() instanceof Sittable && ((Sittable) context.getLootedEntity()).isSitting());
        registerTag("sleeping", context -> context.getLootedEntity() != null && context.getLootedEntity().isSleeping());
        registerTag("spawner-type", SpawnerTypeCondition.class);
        registerTag("spawn-reason", SpawnReasonCondition.class);
        registerTag("tamed", context -> context.getLootedEntity() instanceof Tameable && ((Tameable) context.getLootedEntity()).isTamed());
        registerTag("trading", context -> context.getLootedEntity() instanceof Merchant && ((Merchant) context.getLootedEntity()).isTrading());
        registerTag("vanilla-loot-table", VanillaLootTableCondition.class);
        registerTag("world", WorldCondition.class);

        List<Class<EntityConditions>> classes = ClassUtils.getClassesOf(RoseLoot.getInstance(), PACKAGE_PATH, EntityConditions.class);
        List<String> ignoredLoading = new ArrayList<>();
        for (Class<EntityConditions> clazz : classes) {
            try {
                clazz.getConstructor().newInstance();
            } catch (Exception e) {
                // Log conditions that failed to load
                // This should only be caused by version incompatibilities
                String className = clazz.getSimpleName();
                ignoredLoading.add(className.substring(0, className.length() - 10));
            }
        }
        if (!ignoredLoading.isEmpty())
            RoseLoot.getInstance().getLogger().warning("Ignored loading conditions for: " + ignoredLoading);

        RoseLoot.getInstance().getLogger().info("Registered " + (tagConstructorMap.size() + tagPredicateMap.size()) + " loot table conditions.");
    }

    public static <T extends LootCondition> void registerTag(String prefix, Class<T> tagClass) {
        try {
            tagConstructorMap.put(prefix, tagClass.getConstructor(String.class));
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public static void registerTag(String prefix, Predicate<LootContext> predicate) {
        tagPredicateMap.put(prefix, predicate);
    }

    public static LootCondition parse(String tag) {
        try {
            String parsed = (tag.startsWith("!") ? tag.substring(1) : tag).toLowerCase();
            int index = parsed.indexOf(":");
            String tagPrefix = index == -1 ? parsed : parsed.substring(0, index);

            Constructor<? extends LootCondition> constructor = tagConstructorMap.get(tagPrefix);
            if (constructor != null)
                return constructor.newInstance(tag);

            Predicate<LootContext> predicate = tagPredicateMap.get(tagPrefix);
            if (predicate != null)
                return new BooleanLootCondition(tag, predicate);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

}
