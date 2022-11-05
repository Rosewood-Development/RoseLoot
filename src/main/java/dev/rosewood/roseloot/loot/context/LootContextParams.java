package dev.rosewood.roseloot.loot.context;

import dev.rosewood.roseloot.loot.ExplosionType;
import dev.rosewood.roseloot.loot.LootPlaceholders;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public final class LootContextParams {

    public static final LootContextParam<Location> ORIGIN = create("origin", Location.class, builder ->
            builder.withPlaceholders((x, y) -> {
                Optional.ofNullable(x.getWorld()).ifPresent(world -> {
                    y.add("world", world.getName());
                    y.add("world_time_ticks", world.getTime());
                });
                y.add("x", LootUtils.getToMaximumDecimals(x.getX(), 2));
                y.add("y", LootUtils.getToMaximumDecimals(x.getY(), 2));
                y.add("z", LootUtils.getToMaximumDecimals(x.getZ(), 2));
            }));
    public static final LootContextParam<Boolean> HAS_EXISTING_ITEMS = create("has_existing_items", Boolean.class);
    public static final LootContextParam<Entity> LOOTER = create("looter", Entity.class, builder ->
            builder.withPlayer(x -> x instanceof Player ? (Player) x : null).withItemUsed(LootUtils::getEntityItemUsed));
    public static final LootContextParam<LivingEntity> LOOTED_ENTITY = create("looted_entity", LivingEntity.class, builder ->
            builder.withPlayer(LivingEntity::getKiller).withPlaceholders((x, y) -> {
                y.add("entity_type", x.getType().name().toLowerCase());
                Optional.ofNullable(x.getCustomName()).ifPresent(name -> y.add("entity_name", name));
            }));
    public static final LootContextParam<Block> LOOTED_BLOCK = create("looted_block", Block.class, builder ->
            builder.withPlaceholders((x, y) -> y.add("block_type", x.getType().name().toLowerCase())));
    public static final LootContextParam<BlockData> REPLACED_BLOCK_DATA = create("replaced_block_data", BlockData.class, builder ->
            builder.withPlaceholders((x, y) -> y.add("replaced_block_type", x.getMaterial().name().toLowerCase())));
    public static final LootContextParam<FishHook> FISH_HOOK = create("fish_hook", FishHook.class);
    public static final LootContextParam<ItemStack> INPUT_ITEM = create("input_item", ItemStack.class);
    public static final LootContextParam<NamespacedKey> VANILLA_LOOT_TABLE_KEY = create("vanilla_loot_table_key", NamespacedKey.class, builder ->
            builder.withPlaceholders((x, y) -> y.add("vanilla_loot_table_name", x.toString())));
    public static final LootContextParam<NamespacedKey> ADVANCEMENT_KEY = create("advancement_key", NamespacedKey.class, builder ->
            builder.withPlaceholders((x, y) -> y.add("advancement_name", x.toString())));
    public static final LootContextParam<ExplosionType> EXPLOSION_TYPE = create("explosion_type", ExplosionType.class, builder ->
            builder.withPlaceholders((x, y) -> y.add("explosion_type", x.name().toLowerCase())));

    /**
     * Creates a new {@link LootContextParam} with the given name and type.
     *
     * @param name The name of the parameter
     * @param type The type of the parameter
     * @param <T> The type of the parameter
     * @return The new {@link LootContextParam}
     */
    public static <T> LootContextParam<T> create(String name, Class<T> type) {
        return new LootContextParam<>(name, type);
    }

    /**
     * Creates a new {@link LootContextParam} with the given name and type.
     *
     * @param name The name of the parameter
     * @param type The type of the parameter
     * @param <T> The type of the parameter
     * @param builder A Consumer to build the parameter
     * @return The new {@link LootContextParam}
     */
    public static <T> LootContextParam<T> create(String name, Class<T> type, Consumer<Builder<T>> builder) {
        LootContextParam<T> param = new LootContextParam<>(name, type);
        builder.accept(new Builder<>(param));
        return param;
    }

    public static class Builder<T> {

        private final LootContextParam<T> param;

        private Builder(LootContextParam<T> param) {
            this.param = param;
        }

        public Builder<T> withPlaceholders(BiConsumer<T, LootPlaceholders> placeholderApplicator) {
            this.param.placeholderApplicator = placeholderApplicator;
            return this;
        }

        public Builder<T> withPlayer(Function<T, Player> playerProvider) {
            this.param.playerProvider = playerProvider;
            return this;
        }

        public Builder<T> withItemUsed(Function<T, ItemStack> itemUsedProvider) {
            this.param.itemUsedProvider = itemUsedProvider;
            return this;
        }

    }

}
