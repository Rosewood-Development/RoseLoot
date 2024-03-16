package dev.rosewood.roseloot.loot.context;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.roseloot.loot.LootPlaceholders;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LootContext {

    private final Map<LootContextParam<?>, Object> paramStorage;
    private final double luck;
    private Map<Enchantment, Integer> cachedEnchantmentLevels;
    private final LootPlaceholders placeholders;
    private LootTable currentLootTable;

    private LootContext(double luck, Map<Enchantment, Integer> cachedEnchantmentLevels) {
        this.paramStorage = new LinkedHashMap<>();
        this.luck = luck;
        this.cachedEnchantmentLevels = cachedEnchantmentLevels;
        this.placeholders = new LootPlaceholders();
    }

    /**
     * Puts a LootContextParam value into this LootContext
     *
     * @param param the LootContextParam to put the value for
     * @param value the value to put
     * @param <T> the type of the value
     * @throws IllegalArgumentException if a value with the given LootContextParam already exists
     */
    private <T> void put(LootContextParam<T> param, T value) {
        if (this.paramStorage.put(param, value) != null) {
            throw new IllegalArgumentException("LootContext already contains a value for <param:" + param.getName() + ">");
        } else {
            param.applyPlaceholders(value, this.placeholders);
        }
    }

    /**
     * Gets an Optional LootContext value based on its LootContextParam
     *
     * @param param the LootContextParam to get the value for
     * @param <T> the type of the value
     * @return the value of the LootContextParam
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public <T> Optional<T> get(LootContextParam<T> param) {
        return Optional.ofNullable((T) this.paramStorage.get(param));
    }

    /**
     * Gets an Optional LootContext value cast to a desire class based on its LootContextParam
     *
     * @param param the LootContextParam to get the value for
     * @param clazz the class to cast the value to
     * @param <T> the type of the value
     * @param <R> the type of the desired class
     * @return the value of the LootContextParam
     */
    @SuppressWarnings("unchecked")
    public <T, R> Optional<R> getAs(LootContextParam<T> param, Class<R> clazz) {
        return Optional.ofNullable((T) this.paramStorage.get(param)).map(x -> clazz.isAssignableFrom(x.getClass()) ? (R) x : null);
    }

    /**
     * @return a copy of all the non-null LootContextParams in this context
     */
    public Set<LootContextParam<?>> getParams() {
        return this.paramStorage.entrySet().stream()
                .filter(Objects::nonNull)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * @return the Player that ultimately caused the loot generation
     */
    @NotNull
    public Optional<Player> getLootingPlayer() {
        for (Map.Entry<LootContextParam<?>, Object> entry : this.paramStorage.entrySet()) {
            Optional<Player> player = entry.getKey().getPlayer(entry.getValue());
            if (player.isPresent())
                return player;
        }
        return Optional.empty();
    }

    /**
     * @return the ItemStack used by the looter
     */
    @NotNull
    public Optional<ItemStack> getItemUsed() {
        for (Map.Entry<LootContextParam<?>, Object> entry : this.paramStorage.entrySet()) {
            Optional<ItemStack> itemStack = entry.getKey().getItemUsed(entry.getValue());
            if (itemStack.isPresent())
                return itemStack;
        }
        return Optional.empty();
    }

    /**
     * @return the BlockInfo associated with the looted block
     */
    @NotNull
    public Optional<BlockInfo> getLootedBlockInfo() {
        for (Map.Entry<LootContextParam<?>, Object> entry : this.paramStorage.entrySet()) {
            Optional<BlockInfo> blockInfo = entry.getKey().getBlockInfo(entry.getValue());
            if (blockInfo.isPresent())
                return blockInfo;
        }
        return Optional.empty();
    }

    /**
     * Gets the enchantment level for the given Enchantment for the ItemStack used by the looter
     *
     * @param enchantment The Enchantment level to get
     * @return the enchantment level, or 0 if the ItemStack or Enchantment is not present
     */
    @NotNull
    public int getEnchantmentLevel(Enchantment enchantment) {
        if (this.cachedEnchantmentLevels == null)
            this.getItemUsed().ifPresent(x -> this.cachedEnchantmentLevels = x.getEnchantments());

        if (this.cachedEnchantmentLevels != null)
            return this.cachedEnchantmentLevels.getOrDefault(enchantment, 0);

        return 0;
    }

    /**
     * @return the luck level for this context, used for bonus rolls
     */
    public double getLuckLevel() {
        return this.luck;
    }

    /**
     * Adds a placeholder that can be parsed within this context as %placeholder%.
     * Overwrites existing placeholders in the context with the same key.
     *
     * @param key The placeholder key
     * @param value The placeholder value
     */
    public void addPlaceholder(String key, Object value) {
        this.addPlaceholder(key, value, true);
    }

    /**
     * Adds a placeholder that can be parsed within this context as %placeholder%.
     * Optionally overwrites existing placeholders in the context with the same key.
     *
     * @param key The placeholder key
     * @param value The placeholder value
     * @param overwrite Whether to overwrite existing placeholders
     */
    public void addPlaceholder(String key, Object value, boolean overwrite) {
        if (overwrite || !this.placeholders.containsKey(key))
            this.placeholders.add(key, value);
    }

    /**
     * Formats the text using HexUtils, PlaceholderAPI, and this LootContext's placeholders.
     *
     * @param text the text to format
     * @return the formatted text
     */
    @NotNull
    public String formatText(String text) {
        return HexUtils.colorify(this.applyPlaceholders(text));
    }

    /**
     * Applies placeholders to the text using PlaceholderAPI, and this LootContext's placeholders.
     *
     * @param text the text to apply placeholders to
     * @return the text with placeholders applied
     */
    @NotNull
    public String applyPlaceholders(String text) {
        return PlaceholderAPIHook.applyPlaceholders(this.getLootingPlayer().orElse(null), this.placeholders.apply(text));
    }

    /**
     * Copies placeholders from this LootContext to another LootContext
     *
     * @param otherContext the other LootContext
     */
    public void copyPlaceholders(LootContext otherContext) {
        this.placeholders.copyTo(otherContext.placeholders);
    }

    /**
     * @return the last LootTable to be used with this LootContext
     */
    @NotNull
    public Optional<LootTable> getCurrentLootTable() {
        return Optional.ofNullable(this.currentLootTable);
    }

    /**
     * Sets the LootTable that is currently being generated
     *
     * @param lootTable the LootTable that is currently being generated
     */
    public void setCurrentLootTable(LootTable lootTable) {
        this.currentLootTable = lootTable;
    }

    /**
     * Adds placeholders relative to this context
     */
    private void addContextPlaceholders() {
        this.getLootingPlayer().ifPresent(x -> this.addPlaceholder("player", x.getName()));
        this.getItemUsed().ifPresent(x -> this.addPlaceholder("item_type", x.getType().name().toLowerCase()));
        this.addPlaceholder("luck_level", this.getLuckLevel());
    }

    /**
     * @return a new LootContext builder with a specific luck level
     */
    @NotNull
    public static Builder builder(double luck) {
        return new Builder(luck);
    }

    /**
     * @return a new LootContext builder with a specific luck level and cached enchantment levels
     */
    @NotNull
    public static Builder builder(double luck, Map<Enchantment, Integer> enchantmentLevels) {
        return new Builder(luck, enchantmentLevels);
    }

    /**
     * @return a new LootContext builder with a luck level of 0
     */
    @NotNull
    public static Builder builder() {
        return new Builder(0);
    }

    public static class Builder {

        private final LootContext context;

        private Builder(double luck) {
            this.context = new LootContext(luck, null);
        }

        private Builder(double luck, Map<Enchantment, Integer> enchantmentLevels) {
            this.context = new LootContext(luck, enchantmentLevels);
        }

        /**
         * Puts a LootContextParam value into the LootContext
         *
         * @param param the LootContextParam to put the value for
         * @param value the value to put
         * @param <T> the type of the value
         * @throws IllegalArgumentException if a value with the given LootContextParam already exists
         */
        public <T> Builder put(LootContextParam<T> param, T value) {
            this.context.put(param, value);
            return this;
        }

        /**
         * @return The built LootContext
         */
        public LootContext build() {
            this.context.addContextPlaceholders();
            return this.context;
        }

    }

}
