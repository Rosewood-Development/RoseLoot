package dev.rosewood.roseloot.loot.table;

import com.google.common.collect.Sets;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LootTableType {

    private final Set<LootContextParam<?>> all;
    private final Set<Collection<LootContextParam<?>>> required;
    private final boolean unrestricted;
    private final Set<LootContextParam<?>> extraNotify;

    private LootTableType(Set<LootContextParam<?>> optional, Set<Collection<LootContextParam<?>>> required, boolean unrestricted) {
        this.all = Sets.union(optional, required.stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        this.required = required;
        this.unrestricted = unrestricted;
        this.extraNotify = new HashSet<>();
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

        Set<String> missing = new HashSet<>();
        for (Collection<LootContextParam<?>> required : this.required)
            if (context.getParams().stream().noneMatch(required::contains))
                missing.add(required.stream().map(LootContextParam::getName).collect(Collectors.joining(" OR ")));

        if (!missing.isEmpty())
            throw new IllegalArgumentException("Missing required parameters: [" + String.join(", ", missing) + "]");

        Set<LootContextParam<?>> extra = new HashSet<>(Sets.difference(context.getParams(), this.all));
        if (!extra.isEmpty()) {
            Set<LootContextParam<?>> newExtra = new HashSet<>(Sets.difference(extra, this.extraNotify));
            if (!newExtra.isEmpty()) {
                this.extraNotify.addAll(newExtra);
                RoseLoot.getInstance().getLogger().info("Loaded extra parameters in LootContext: [" + newExtra.stream().map(LootContextParam::getName).collect(Collectors.joining(", ")) + "]");
            }
        }
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

        private final Set<LootContextParam<?>> optional;
        private final Set<Collection<LootContextParam<?>>> required;

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
         * Adds a parameter to the required parameters.
         * If more than one parameter is provided, at least one of them must be provided.
         *
         * @param params The parameter to add
         * @return This builder
         */
        public Builder required(LootContextParam<?>... params) {
            this.required.add(Arrays.asList(params));
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
