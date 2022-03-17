package dev.rosewood.roseloot.loot.context;

import dev.rosewood.roseloot.loot.LootPlaceholders;
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

    public LootContextParam(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public void applyPlaceholders(Object value, LootPlaceholders placeholders) {
        if (this.placeholderApplicator != null)
            this.transform(value).ifPresent(x -> this.placeholderApplicator.accept(x, placeholders));
    }

    public Optional<Player> getPlayer(Object value) {
        if (this.playerProvider != null)
            return this.transform(value).map(x -> this.playerProvider.apply(x));
        return Optional.empty();
    }

    public Optional<ItemStack> getItemUsed(Object value) {
        if (this.itemUsedProvider != null)
            return this.transform(value).map(x -> this.itemUsedProvider.apply(x));
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<T> transform(Object value) {
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
