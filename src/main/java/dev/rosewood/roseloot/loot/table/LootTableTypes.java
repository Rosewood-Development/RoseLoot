package dev.rosewood.roseloot.loot.table;

import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LootTableTypes {

    private static final Map<String, LootTableType> DEFAULT_LOOT_TABLE_TYPES = new HashMap<>();

    public static final LootTableType ENTITY = register("ENTITY", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTED_ENTITY)
                    .optional(LootContextParams.LOOTER).optional(LootContextParams.EXPLOSION_TYPE).optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType BLOCK = register("BLOCK", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTED_BLOCK)
                    .optional(LootContextParams.LOOTER).optional(LootContextParams.EXPLOSION_TYPE).optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType FISHING = register("FISHING", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTER).required(LootContextParams.FISH_HOOK)
                    .optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType CONTAINER = register("CONTAINER", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTED_BLOCK).required(LootContextParams.VANILLA_LOOT_TABLE_KEY)
                    .optional(LootContextParams.LOOTER).optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType PIGLIN_BARTER = register("PIGLIN_BARTER", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTED_ENTITY).required(LootContextParams.INPUT_ITEM)
                    .optional(LootContextParams.LOOTER).optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType ENTITY_DROP_ITEM = register("ENTITY_DROP_ITEM", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTED_ENTITY).required(LootContextParams.INPUT_ITEM)
                    .optional(LootContextParams.LOOTER).optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType ADVANCEMENT = register("ADVANCEMENT", builder ->
            builder.required(LootContextParams.ORIGIN).required(LootContextParams.LOOTER).required(LootContextParams.ADVANCEMENT_KEY)
                    .optional(LootContextParams.HAS_EXISTING_ITEMS));
    public static final LootTableType LOOT_TABLE = register("LOOT_TABLE", LootTableType.unrestricted());

    public static Map<String, LootTableType> values() {
        return Collections.unmodifiableMap(DEFAULT_LOOT_TABLE_TYPES);
    }

    private static LootTableType register(String name, LootTableType lootTableType) {
        DEFAULT_LOOT_TABLE_TYPES.put(name, lootTableType);
        return lootTableType;
    }

    private static LootTableType register(String name, Consumer<LootTableType.Builder> builderConsumer) {
        LootTableType.Builder builder = LootTableType.builder();
        builderConsumer.accept(builder);
        LootTableType lootTableType = builder.build();
        DEFAULT_LOOT_TABLE_TYPES.put(name, lootTableType);
        return lootTableType;
    }

}
