package dev.rosewood.roseloot.loot.context;

import dev.rosewood.roseloot.loot.LootPlaceholders;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootContextParam<T> {

    private final String name;
    private final Class<T> type;
    protected BiConsumer<T, LootPlaceholders> placeholderApplicator;
    protected Function<T, Player> playerProvider;
    protected Function<T, ItemStack> itemUsedProvider;
    protected Function<T, BlockInfo> blockInfoProvider;

    public LootContextParam(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name of this parameter
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the type of this parameter
     */
    public Class<T> getType() {
        return this.type;
    }

    /**
     * Applies any placeholders for this parameter value to the given LootPlaceholders instance
     *
     * @param value the value to apply placeholders with
     * @param placeholders the placeholders to apply to
     */
    public void applyPlaceholders(Object value, LootPlaceholders placeholders) {
        if (this.placeholderApplicator != null)
            this.transform(value).ifPresent(x -> this.placeholderApplicator.accept(x, placeholders));
    }

    /**
     * Gets an optional player from the given value
     *
     * @param value the value to get the player from
     * @return an optional player
     */
    public Optional<Player> getPlayer(Object value) {
        if (this.playerProvider != null)
            return this.transform(value).map(x -> this.playerProvider.apply(x));
        return Optional.empty();
    }

    /**
     * Gets an optional item used from the given value
     *
     * @param value the value to get the item from
     * @return an optional item
     */
    public Optional<ItemStack> getItemUsed(Object value) {
        if (this.itemUsedProvider != null)
            return this.transform(value).map(x -> this.itemUsedProvider.apply(x));
        return Optional.empty();
    }

    /**
     * Gets optional block info used from the given value
     *
     * @param value the value to get the item from
     * @return an optional item
     */
    public Optional<BlockInfo> getBlockInfo(Object value) {
        if (this.blockInfoProvider != null)
            return this.transform(value).map(x -> this.blockInfoProvider.apply(x));
        return Optional.empty();
    }

    /**
     * Transforms the given value into the type of this parameter
     *
     * @param value the value to transform
     * @return an optional transformed value
     */
    @SuppressWarnings("unchecked")
    private Optional<T> transform(Object value) {
        if (value == null)
            return Optional.empty();
        if (this.type.isAssignableFrom(value.getClass()))
            return Optional.of((T) value);
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        LootContextParam<?> that = (LootContextParam<?>) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

}
