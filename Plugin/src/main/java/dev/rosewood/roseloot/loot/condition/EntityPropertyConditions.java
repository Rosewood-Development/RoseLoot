package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.rosegarden.compatibility.CompatibilityAdapter;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import dev.rosewood.roseloot.util.LootUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.GlowSquid;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Sniffer;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;

public class EntityPropertyConditions {

    private static final List<EntityProperties<?, ?>> ENTITY_PROPERTIES;
    private static final Map<Class<?>, Method> KEYED_VALUE_METHOD_LOOKUP_MAP;

    static {
        ENTITY_PROPERTIES = new ArrayList<>();
        KEYED_VALUE_METHOD_LOOKUP_MAP = new HashMap<>();

        // Register conditions for specific entities
        if (NMSUtil.getVersionNumber() >= 21) {
            if (NMSUtil.getMinorVersionNumber() >= 4) {
                registerBoolean(Bogged.class, "sheared", Bogged::isSheared);
            }

            if (NMSUtil.getMinorVersionNumber() >= 5) {
                registerEnum(Chicken.class, "variant", Chicken::getVariant, Chicken.Variant.class);
                // hot garbage served right up courtesy of spigot 1.21.5 commodore rewrites, what's a cow, anyway?
                registerEnum(Cow.class, "org.bukkit.entity.Cow", "variant", x -> getReturnValueReflectively(Cow.class, "org.bukkit.entity.Cow", x, Cow.Variant.class, "getVariant"), Cow.Variant.class);
                registerEnum(Pig.class, "variant", Pig::getVariant, Pig.Variant.class);
            }
        }

        if (NMSUtil.getVersionNumber() >= 20) {
            registerEnum(Sniffer.class, "state", Sniffer::getState, Sniffer.State.class);

            if (NMSUtil.getVersionNumber() > 20 || NMSUtil.getMinorVersionNumber() == 6) {
                registerEnum(Wolf.class, "variant", Wolf::getVariant, Wolf.Variant.class);
            }
        }

        if (NMSUtil.getVersionNumber() >= 19) {
            registerEnum(Frog.class, "variant", Frog::getVariant, Frog.Variant.class);
            registerBoolean(Goat.class, "has-left-horn", Goat::hasLeftHorn);
            registerBoolean(Goat.class, "has-right-horn", Goat::hasRightHorn);

            try {
                Class.forName("org.bukkit.entity.Camel");
                registerBoolean(Camel.class, "dashing", Camel::isDashing);
            } catch (ClassNotFoundException ignored) { }
        }

        if (NMSUtil.getVersionNumber() >= 17) {
            registerBoolean(Axolotl.class, "playing-dead", Axolotl::isPlayingDead);
            registerEnum(Axolotl.class, "variant", Axolotl::getVariant, Axolotl.Variant.class);
            registerBoolean(GlowSquid.class, "dark", x -> x.getDarkTicksRemaining() > 0);
            registerBoolean(Goat.class, "screaming", Goat::isScreaming);
            registerBoolean(Skeleton.class, "converting", Skeleton::isConverting);
        }

        registerBoolean(Bat.class, "sleeping", x -> !x.isAwake());
        registerBoolean(Bee.class, "angry", x -> x.getAnger() > 0);
        registerBoolean(Bee.class, "has-hive", x -> x.getHive() != null);
        registerBoolean(Bee.class, "has-stung", Bee::hasStung);
        registerBoolean(Bee.class, "has-flower", x -> x.getFlower() != null);
        registerBoolean(Bee.class, "has-nectar", Bee::hasNectar);
        registerEnum(Cat.class, "type", Cat::getCatType, Cat.Type.class);
        registerEnum(Cat.class, "collar-color", Cat::getCollarColor, DyeColor.class);
        registerBoolean(Creeper.class, "charged", Creeper::isPowered);
        registerEnum(EnderDragon.class, "phase", EnderDragon::getPhase, EnderDragon.Phase.class);
        registerMaterial(Enderman.class, "holding-block", x -> x.getCarriedBlock() == null ? null : x.getCarriedBlock().getMaterial(), true, true);
        registerEnum(Fox.class, "type", Fox::getFoxType, Fox.Type.class);
        registerBoolean(Fox.class, "crouching", Fox::isCrouching);
        registerBoolean(Hoglin.class, "unhuntable", x -> !x.isAbleToBeHunted());
        registerEnum(Horse.class, "armored", x -> HorseArmorType.from(x.getInventory().getArmor()), HorseArmorType.class, true);
        registerEnum(Horse.class, "style", Horse::getStyle, Horse.Style.class);
        registerEnum(Horse.class, "color", Horse::getColor, Horse.Color.class);
        registerBoolean(Husk.class, "converting", Husk::isConverting);
        registerEnum(Illusioner.class, "spell", Illusioner::getSpell, Illusioner.Spell.class);
        registerBoolean(IronGolem.class, "player-created", IronGolem::isPlayerCreated);
        registerMaterial(Llama.class, "decor", x -> x.getInventory().getDecor() == null ? null : x.getInventory().getDecor().getType(), true, true);
        registerEnum(Llama.class, "color", Llama::getColor, Llama.Color.class);
        registerInt(MagmaCube.class, "size", MagmaCube::getSize);
        registerEnum(MushroomCow.class, "variant", MushroomCow::getVariant, MushroomCow.Variant.class);
        registerEnum(Panda.class, "main-gene", Panda::getMainGene, Panda.Gene.class);
        registerEnum(Panda.class, "hidden-gene", Panda::getHiddenGene, Panda.Gene.class);
        registerEnum(Parrot.class, "variant", Parrot::getVariant, Parrot.Variant.class);
        registerInt(Phantom.class, "size", Phantom::getSize);
        registerBoolean(PiglinBrute.class, "converting", PiglinBrute::isConverting);
        registerBoolean(PiglinBrute.class, "immune-to-zombification", PiglinBrute::isImmuneToZombification);
        registerBoolean(Piglin.class, "converting", Piglin::isConverting);
        registerBoolean(Piglin.class, "immune-to-zombification", Piglin::isImmuneToZombification);
        registerBoolean(Piglin.class, "unable-to-hunt", x -> !x.isAbleToHunt());
        registerBoolean(PigZombie.class, "angry", PigZombie::isAngry);
        registerInt(PufferFish.class, "puff-state", PufferFish::getPuffState);
        registerEnum(Rabbit.class, "type", Rabbit::getRabbitType, Rabbit.Type.class);
        registerBoolean(Sheep.class, "sheared", CompatibilityAdapter.getShearedHandler()::isSheared);
        registerEnum(Sheep.class, "color", Sheep::getColor, DyeColor.class);
        registerInt(Slime.class, "size", Slime::getSize);
        registerBoolean(Snowman.class, "no-pumpkin", Snowman::isDerp);
        registerBoolean(Strider.class, "shivering", Strider::isShivering);
        registerMaterial(TraderLlama.class, "decor", x -> x.getInventory().getDecor() == null ? null : x.getInventory().getDecor().getType(), true, true);
        registerEnum(TraderLlama.class, "color", TraderLlama::getColor, TraderLlama.Color.class);
        registerEnum(TropicalFish.class, "body-color", TropicalFish::getBodyColor, DyeColor.class);
        registerEnum(TropicalFish.class, "pattern", TropicalFish::getPattern, TropicalFish.Pattern.class);
        registerEnum(TropicalFish.class, "pattern-color", TropicalFish::getPatternColor, DyeColor.class);
        registerBoolean(Vex.class, "charging", Vex::isCharging);
        registerEnum(Villager.class, "profession", Villager::getProfession, Villager.Profession.class);
        registerEnum(Villager.class, "type", Villager::getVillagerType, Villager.Type.class);
        registerInt(Villager.class, "level", Villager::getVillagerLevel);
        registerBoolean(Wolf.class, "angry", Wolf::isAngry);
        registerEnum(Wolf.class, "collar-color", Wolf::getCollarColor, DyeColor.class);
        registerBoolean(Zombie.class, "converting", Zombie::isConverting);
        registerBoolean(ZombieVillager.class, "converting", ZombieVillager::isConverting);
        registerEnum(ZombieVillager.class, "profession", ZombieVillager::getVillagerProfession, Villager.Profession.class);
    }

    public static void apply(LootConditionRegistrationEvent event, String prefix, LootContextParam<? extends Entity> entityContext) {
        ENTITY_PROPERTIES.forEach(x -> event.registerLootCondition(prefix + x.key(), tag -> new EntityPropertyLootCondition<>(tag, x, entityContext)));
    }

    private static <T extends Entity, V> void register(Class<T> entityClass, String name, BiPredicate<T, Collection<V>> predicate, @Nullable Function<String[], Collection<V>> valuesValidator, boolean allowEmptyValues) {
        ENTITY_PROPERTIES.add(new EntityProperties<>(entityClass, name, predicate, valuesValidator, allowEmptyValues));
    }

    private static <T extends Entity> void registerBoolean(Class<T> entityClass, String name, Function<T, Boolean> supplier) {
        register(entityClass, name, (entity, values) -> supplier.apply(entity), null, false);
    }

    private static <T extends Entity> void registerMaterial(Class<T> entityClass, String name, Function<T, Material> supplier, boolean onlyBlocks, boolean allowEmptyValues) {
        register(entityClass, name, (entity, values) -> availableOrContains(entity, values, supplier, allowEmptyValues), values -> {
            List<Material> materials = new ArrayList<>();
            for (String value : values) {
                try {
                    Material material = Material.matchMaterial(value);
                    if (material != null && (!onlyBlocks || material.isBlock()))
                        materials.add(material);
                } catch (Exception ignored) { }
            }
            return materials;
        }, allowEmptyValues);
    }

    // Oh yeah, this is a mess, but it makes the registration look clean.
    @SuppressWarnings({"unchecked", "deprecation"})
    private static <T extends Entity, E> void registerEnum(Class<T> entityClass, String name, Function<T, E> supplier, Class<E> enumClass, boolean allowEmptyValues) {
        if (NMSUtil.getVersionNumber() >= 21 && Keyed.class.isAssignableFrom(enumClass)) {
            Class<? extends Keyed> keyedClass = (Class<? extends Keyed>) enumClass;
            Registry<?> registry = Bukkit.getRegistry(keyedClass);

            if (registry != null) {
                register(entityClass, name, (entity, values) -> availableOrContainsUnchecked(entity, values, supplier, false), values -> {
                    Set<Object> registryValues = new HashSet<>();
                    for (String value : values) {
                        try {
                            NamespacedKey key = NamespacedKey.fromString(value);
                            if (key != null)
                                registryValues.add(registry.getOrThrow(key));
                        } catch (Exception ignored) { }
                    }
                    return registryValues;
                }, false);
                return;
            }
        }

        register(entityClass, name, (entity, values) -> availableOrContainsUnchecked(entity, values, supplier, allowEmptyValues), values -> {
            Set<Object> enumValues = new HashSet<>();
            for (String value : values) {
                try {
                    for (Object enumValue : enumClass.getEnumConstants()) {
                        Enum<?> enumConstant = (Enum<?>) enumValue;
                        if (value.equalsIgnoreCase(enumConstant.name())) {
                            enumValues.add(enumConstant);
                            break;
                        }
                    }
                } catch (Exception ignored) { }
            }
            return enumValues;
        }, allowEmptyValues);
    }

    private static <T extends Entity, E> void registerEnum(Class<T> entityClass, String name, Function<T, E> supplier, Class<E> enumClass) {
        registerEnum(entityClass, name, supplier, enumClass, false);
    }

    private static <T extends Entity, E> void registerEnum(Class<T> entityClass0, String className, String name, Function<T, E> supplier, Class<E> enumClass) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(className);
            registerEnum(clazz, name, supplier, enumClass, false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Entity> void registerInt(Class<T> entityClass, String name, Function<T, Integer> supplier) {
        register(entityClass, name, (entity, values) -> values.contains(supplier.apply(entity)), values -> {
            List<Integer> intValues = new ArrayList<>();
            for (String value : values) {
                try {
                    intValues.add(Integer.parseInt(value));
                } catch (Exception ignored) { }
            }
            return intValues;
        }, false);
    }

    private static <T extends Entity, V> boolean availableOrContains(T entity, Collection<V> values, Function<T, V> supplier, boolean allowEmptyValues) {
        V value = supplier.apply(entity);
        if (allowEmptyValues && values.isEmpty())
            return value != null;
        return values.contains(value);
    }

    private static <T extends Entity> boolean availableOrContainsUnchecked(T entity, Collection<Object> values, Function<T, ?> supplier, boolean allowEmptyValues) {
        Object value = supplier.apply(entity);
        if (allowEmptyValues && values.isEmpty())
            return value != null;
        return values.contains(value);
    }

    // Fix for java.lang.NoSuchMethodError: 'org.bukkit.entity.Cow$Variant org.bukkit.entity.AbstractCow.getVariant()' on 1.21.5 due to Spigot rewrites
    @SuppressWarnings("unchecked")
    private static <T, R> R getReturnValueReflectively(Class<T> classType0, String className, T value, Class<R> returnType, String methodName) {
        try {
            Class<?> classType = Class.forName(className);
            Method method = KEYED_VALUE_METHOD_LOOKUP_MAP.get(classType);
            if (method == null) {
                method = classType.getMethod(methodName);
                KEYED_VALUE_METHOD_LOOKUP_MAP.put(classType, method);
            }
            return (R) method.invoke(value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public record EntityProperties<T extends Entity, V>(Class<T> entityClass, String name, BiPredicate<T, Collection<V>> predicate, @Nullable Function<String[], Collection<V>> valuesValidator, boolean allowEmptyValues) {

        public String key() {
            EntityType entityType = LootUtils.ENTITY_CLASS_TO_TYPE.get(this.entityClass);
            if (entityType == null)
                throw new IllegalArgumentException("Invalid entity class: " + this.entityClass.getName());
            return entityType.getKey().getKey().replace('_', '-') + '-' + this.name;
        }

    }

    public static class EntityPropertyLootCondition<T extends Entity, V> extends BaseLootCondition {

        private final EntityProperties<T, V> properties;
        private final LootContextParam<? extends Entity> entityContext;
        private Collection<V> values;

        public EntityPropertyLootCondition(String tag, EntityProperties<T, V> properties, LootContextParam<? extends Entity> entityContext) {
            super(tag, false);
            this.properties = properties;
            this.entityContext = entityContext;
            this.init(tag, true);
        }

        @Override
        public boolean check(LootContext context) {
            return context.getAs(this.entityContext, this.properties.entityClass())
                    .filter(entity -> this.properties.predicate().test(entity, this.values))
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            if (this.properties.valuesValidator() == null)
                return values.length == 0;

            this.values = this.properties.valuesValidator().apply(values);
            return this.properties.allowEmptyValues() || !this.values.isEmpty();
        }

    }

    private enum HorseArmorType {
        DIAMOND("DIAMOND_HORSE_ARMOR"),
        GOLD("GOLDEN_HORSE_ARMOR"),
        IRON("IRON_HORSE_ARMOR"),
        LEATHER("LEATHER_HORSE_ARMOR");

        private final String material;

        HorseArmorType(String material) {
            this.material = material;
        }

        public static HorseArmorType from(ItemStack item) {
            if (item == null)
                return null;

            for (HorseArmorType value : values())
                if (value.material.equalsIgnoreCase(item.getType().name()))
                    return value;
            return null;
        }
    }

}
