package dev.rosewood.roseloot.util;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.ExplosionType;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

@SuppressWarnings("unchecked")
public final class LootUtils {

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols();
    static {
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator('.');
    }

    private LootUtils() {

    }

    public static final Random RANDOM = new Random();
    private static final String SPAWN_REASON_METADATA_NAME = "spawn_reason";
    private static final String REGEX_DECOLORIZE_HEX = "&x&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])";
    public static final Map<String, Color> FIREWORK_COLORS = new HashMap<>() {{
        this.put("WHITE", Color.WHITE);
        this.put("SILVER", Color.SILVER);
        this.put("GRAY", Color.GRAY);
        this.put("BLACK", Color.BLACK);
        this.put("RED", Color.RED);
        this.put("MAROON", Color.MAROON);
        this.put("YELLOW", Color.YELLOW);
        this.put("OLIVE", Color.OLIVE);
        this.put("LIME", Color.LIME);
        this.put("GREEN", Color.GREEN);
        this.put("AQUA", Color.AQUA);
        this.put("TEAL", Color.TEAL);
        this.put("BLUE", Color.BLUE);
        this.put("NAVY", Color.NAVY);
        this.put("FUCHSIA", Color.FUCHSIA);
        this.put("PURPLE", Color.PURPLE);
        this.put("ORANGE", Color.ORANGE);
    }};

    public static final Map<Class<? extends LivingEntity>, EntityType> ENTITY_CLASS_TO_TYPE;
    static {
        ENTITY_CLASS_TO_TYPE = new HashMap<>();
        for (EntityType entityType : EntityType.values()) {
            Class<? extends Entity> entityClass = entityType.getEntityClass();
            if (entityClass != null)
                ENTITY_CLASS_TO_TYPE.put((Class<? extends LivingEntity>) entityType.getEntityClass(), entityType);
        }
    }

    /**
     * Checks if a chance between 0-1 passes
     *
     * @param chance The chance
     * @return true if the chance passed, otherwise false
     */
    public static boolean checkChance(double chance) {
        return RANDOM.nextDouble() <= chance;
    }

    /**
     * Check if a durability decrease by 1 should be ignored
     *
     * @param level The level of the unbreaking enchantment
     * @return true if a durability decrease by 1 should be ignored, false otherwise
     */
    public static boolean shouldIgnoreDurabilityDecrease(int level) {
        return RANDOM.nextInt(level + 1) > 0;
    }

    /**
     * Gets a random value between the given range, inclusively
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A value between the min and max, inclusively
     */
    public static int randomInRange(int min, int max) {
        if (min == max)
            return min;

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * Gets a random value between the given range, inclusively
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A value between the min and max, inclusively
     */
    public static double randomInRange(double min, double max) {
        if (min == max)
            return min;

        if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }
        return RANDOM.nextDouble() * (max - min) + min;
    }

    /**
     * Sets the spawn reason for the given LivingEntity.
     * Does not overwrite an existing spawn reason.
     *
     * @param entity The entity to set the spawn reason of
     * @param spawnReason The spawn reason to set
     */
    public static void setEntitySpawnReason(LivingEntity entity, SpawnReason spawnReason) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(RoseLoot.getInstance(), SPAWN_REASON_METADATA_NAME);
        dataContainer.set(key, PersistentDataType.STRING, spawnReason.name());
    }

    /**
     * Gets the spawn reason of the given LivingEntity
     *
     * @param entity The entity to get the spawn reason of
     * @return The SpawnReason, or SpawnReason.CUSTOM if none is saved
     */
    public static SpawnReason getEntitySpawnReason(LivingEntity entity) {
        String reason = entity.getPersistentDataContainer().get(new NamespacedKey(RoseLoot.getInstance(), SPAWN_REASON_METADATA_NAME), PersistentDataType.STRING);
        SpawnReason spawnReason;
        if (reason != null) {
            try {
                spawnReason = SpawnReason.valueOf(reason);
            } catch (Exception ex) {
                spawnReason = SpawnReason.CUSTOM;
            }
        } else {
            spawnReason = SpawnReason.CUSTOM;
        }
        return spawnReason;
    }

    public static List<File> listFiles(File current, List<String> excludedDirectories, List<String> extensions) {
        List<File> listedFiles = new ArrayList<>();
        File[] files = current.listFiles();
        if (files == null)
            return listedFiles;

        for (File file : files) {
            if (file.isDirectory() && !excludedDirectories.contains(file.getName())) {
                listedFiles.addAll(listFiles(file, excludedDirectories, extensions));
            } else if (file.isFile() && extensions.stream().anyMatch(x -> file.getName().endsWith(x))) {
                listedFiles.add(file);
            }
        }

        return listedFiles;
    }

    public static String getFileName(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Propagates the killer up the stack until we find the ultimate cause of an Entity's death
     *
     * @param entity The Entity to propagate up from
     * @return The Entity that ultimated caused the death
     */
    public static Entity propagateKiller(Entity entity) {
        if (entity instanceof LivingEntity && ((LivingEntity) entity).getKiller() != null)
            return ((LivingEntity) entity).getKiller();

        if (entity instanceof TNTPrimed tntPrimed) {
            // Propagate the igniter of the tnt up the stack
            Entity tntSource = tntPrimed.getSource();
            if (tntSource != null)
                entity = tntSource;
        }

        if (entity instanceof Projectile projectile) {
            // Check for the projectile type first, if not fall back to the shooter
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Entity)
                entity = (Entity) source;
        }

        if (entity instanceof Tameable tameable) {
            // Propagate to the tamed entity's owner (if they're online)
            AnimalTamer tamer = tameable.getOwner();
            if (tamer != null) {
                Player player = Bukkit.getPlayer(tamer.getUniqueId());
                if (player != null)
                    entity = player;
            }
        }

        return entity;
    }

    /**
     * Converts Minecraft color code characters into ampersands
     *
     * @param string The string to convert
     * @return a copy of the String with Minecraft color code characters replaced as ampersands
     */
    public static String decolorize(String string) {
        if (string == null || string.isEmpty())
            return string;
        return string.replace(ChatColor.COLOR_CHAR, '&').replaceAll(REGEX_DECOLORIZE_HEX, "#$1$2$3$4$5$6");
    }

    public static String getToMaximumDecimals(double value, int decimals) {
        DecimalFormat decimalFormat = new DecimalFormat("0." + new String(new char[decimals]).replace('\0', '#'), DECIMAL_FORMAT_SYMBOLS);
        decimalFormat.setGroupingUsed(false);
        return decimalFormat.format(value);
    }

    public static <T extends Keyed> Set<T> getTagValues(String value, Class<T> clazz, String registry) {
        Tag<T> tag = getTag(value, clazz, registry);
        return tag == null ? null : tag.getValues();
    }

    public static <T extends Keyed> Tag<T> getTag(String value, Class<T> clazz, String registry) {
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key != null)
            return Bukkit.getTag(registry, key, clazz);
        return null;
    }

    /**
     * Gets an Entity's luck level
     *
     * @param entity The Entity to get the luck level of
     * @param includeFishingLuck Whether to include fishing luck
     * @return The luck level of the LivingEntity
     */
    public static double getEntityLuck(Entity entity, boolean includeFishingLuck) {
        if (!(entity instanceof LivingEntity livingEntity))
            return 0;

        double luck = 0;
        AttributeInstance attribute = livingEntity.getAttribute(VersionUtils.LUCK);
        if (attribute != null)
            luck += attribute.getValue();

        if (includeFishingLuck) {
            EntityEquipment equipment = livingEntity.getEquipment();
            if (equipment != null) {
                if (equipment.getItemInMainHand().getType() == Material.FISHING_ROD && equipment.getItemInMainHand().getItemMeta() != null) {
                    luck += equipment.getItemInMainHand().getItemMeta().getEnchantLevel(VersionUtils.LUCK_OF_THE_SEA);
                } else if (equipment.getItemInOffHand().getType() == Material.FISHING_ROD && equipment.getItemInOffHand().getItemMeta() != null) {
                    luck += equipment.getItemInOffHand().getItemMeta().getEnchantLevel(VersionUtils.LUCK_OF_THE_SEA);
                }
            }
        }

        return luck;
    }

    /**
     * Gets an Entity's luck level
     *
     * @param entity The Entity to get the luck level of
     * @return The luck level of the Entity
     */
    public static double getEntityLuck(Entity entity) {
        return getEntityLuck(entity, false);
    }

    /**
     * Gets the item used by an entity
     *
     * @param entity The entity to get the item from
     * @return The item used by the entity
     */
    public static ItemStack getEntityItemUsed(Entity entity) {
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter)
            entity = shooter;

        if (!(entity instanceof LivingEntity livingEntity))
            return null;

        EntityEquipment equipment = livingEntity.getEquipment();
        if (equipment == null)
            return null;

        if (equipment.getItemInMainHand().getType() != Material.AIR)
            return equipment.getItemInMainHand();

        if (equipment.getItemInOffHand().getType() != Material.AIR)
            return equipment.getItemInOffHand();

        return null;
    }

    /**
     * Gets the explosion type from a killed entity
     *
     * @param entity The killed entity
     * @return The explosion type
     */
    public static ExplosionType getDeathExplosionType(LivingEntity entity) {
        EntityDamageEvent event = entity.getLastDamageCause();
        if (event == null)
            return null;

        return switch (event.getCause()) {
            case BLOCK_EXPLOSION -> ExplosionType.BLOCK;
            case ENTITY_EXPLOSION -> {
                if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof Creeper creeper)
                    yield creeper.isPowered() ? ExplosionType.CHARGED_ENTITY : ExplosionType.ENTITY;
                yield ExplosionType.ENTITY;
            }
            default -> null;
        };
    }

    /**
     * Plays the item break animation and sound to a player
     *
     * @param player The player to play the animation to
     * @param itemStack The item to play the animation with
     */
    public static void playItemBreakAnimation(Player player, ItemStack itemStack) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.8f, 0.8f + RANDOM.nextFloat() * 0.4f);
        Location location = player.getLocation();
        for (int i = 0; i < 5; i++) {
            Vector offset = new Vector((RANDOM.nextDouble() - 0.5) * 0.1, RANDOM.nextDouble() * 0.1 + 0.1, 0.0)
                    .rotateAroundX(-Math.toRadians(location.getPitch()))
                    .rotateAroundY(-Math.toRadians(location.getYaw()))
                    .add(new Vector(0, 0.05, 0));
            Vector position = new Vector((RANDOM.nextDouble() - 0.5) * 0.3, -RANDOM.nextDouble() * 0.6 - 0.3, 0.6)
                    .rotateAroundX(-Math.toRadians(location.getPitch()))
                    .rotateAroundY(-Math.toRadians(location.getYaw()))
                    .add(new Vector(player.getLocation().getX(), player.getLocation().getY() + player.getEyeHeight(), player.getLocation().getZ()));
            player.spawnParticle(VersionUtils.PARTICLE_ITEM, position.toLocation(location.getWorld()), 1, offset.getX(), offset.getY(), offset.getZ(), 0, itemStack);
        }
    }

    public static List<ItemStack> createItemStackCopies(ItemStack itemStack, int amount) {
        if (itemStack == null || amount <= 0)
            return new ArrayList<>();

        List<ItemStack> items = new ArrayList<>();
        int maxStackSize = itemStack.getMaxStackSize();
        while (maxStackSize > 0 && amount > 0) {
            ItemStack clone = itemStack.clone();
            if (amount > maxStackSize) {
                clone.setAmount(maxStackSize);
                items.add(clone);
                amount -= maxStackSize;
            } else {
                clone.setAmount(amount);
                items.add(clone);
                amount = 0;
            }
        }
        return items;
    }

}
