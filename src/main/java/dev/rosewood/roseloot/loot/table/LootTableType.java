package dev.rosewood.roseloot.loot.table;

import com.google.common.collect.Sets;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LootTableType {

    private final Set<LootContextParam<?>> all;
    private final Set<LootContextParam<?>> required;
    private final boolean unrestricted;

    private LootTableType(Set<LootContextParam<?>> optional, Set<LootContextParam<?>> required, boolean unrestricted) {
        this.all = Sets.union(optional, required);
        this.required = required;
        this.unrestricted = unrestricted;
    }

    /**
     * Validates that the parameters in a LootContext are valid for this LootTableType
     *
     * @param context The LootContext to validate
     * @throws IllegalArgumentException If the LootContext parameters are invalid
     */
    public void validateLootContext(LootContext context) {
        if (this.unrestricted)
            return;

        Set<LootContextParam<?>> missing = Sets.difference(this.required, context.getParams());
        if (!missing.isEmpty())
            throw new IllegalArgumentException("Missing required parameters: " + missing.stream().map(LootContextParam::getName).collect(Collectors.joining(", ")));

        Set<LootContextParam<?>> extra = Sets.difference(context.getParams(), this.all);
        if (!extra.isEmpty())
            throw new IllegalArgumentException("Extra parameters: " + extra.stream().map(LootContextParam::getName).collect(Collectors.joining(", ")));
    }

    /**
     * @return a new LootTableType builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return a new LootTableType builder which accepts all parameters
     */
    public static LootTableType unrestricted() {
        return new LootTableType(Set.of(), Set.of(), true);
    }

    public static class Builder {

        private final Set<LootContextParam<?>> optional, required;

        private Builder() {
            this.optional = new HashSet<>();
            this.required = new HashSet<>();
        }

        /**
         * Adds a parameter to the optional parameters
         *
         * @param param The parameter to add
         * @return This builder
         */
        public Builder optional(LootContextParam<?> param) {
            this.optional.add(param);
            return this;
        }

        /**
         * Adds a parameter to the required parameters
         *
         * @param param The parameter to add
         * @return This builder
         */
        public Builder required(LootContextParam<?> param) {
            this.required.add(param);
            return this;
        }

        /**
         * @return A new LootTableType with the given parameters
         */
        public LootTableType build() {
            return new LootTableType(this.optional, this.required, false);
        }

    }

}
