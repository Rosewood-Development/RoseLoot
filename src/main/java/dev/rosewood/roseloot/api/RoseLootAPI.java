package dev.rosewood.roseloot.api;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public final class RoseLootAPI {

    private static RoseLootAPI instance;

    private final Map<String, Function<String, LootCondition>> customRegisteredConditions;

    private RoseLootAPI() {
        this.customRegisteredConditions = new LinkedHashMap<>();
    }

    public static RoseLootAPI getInstance() {
        if (instance == null)
            instance = new RoseLootAPI();
        return instance;
    }

    /**
     * Registers a custom LootCondition with the given prefix and tag parser.
     * This LootCondition will automatically be loaded when RoseLoot enables or reloads.
     *
     * @param name The prefix of the condition
     * @param parser The parser for the condition.
     *               The function parser should fulfill the following contract:
     *               - The argument is the unparsed tag entered as a condition
     *               - The function should accept and parse any condition tag that starts with the given name
     *               - The function should return a LootCondition that will be used to check the condition
     *               - The function should return null or throw an IllegalArgumentException if the tag is invalid
     * @see LootConditionRegistrationEvent for an alternative registration method
     * @return true if registering the new LootCondition overwrote a different LootCondition with the same name, false otherwise
     */
    public boolean registerCustomLootCondition(String name, Function<String, LootCondition> parser) {
        return this.customRegisteredConditions.put(name, parser) != null;
    }

    /**
     * Unregisters a custom LootCondition.
     *
     * @param name The name of the LootCondition to unregister
     * @return true if a LootCondition was unregistered, false otherwise
     */
    public boolean unregisterCustomLootCondition(String name) {
        return this.customRegisteredConditions.remove(name) != null;
    }

    /**
     * @return An unmodifiable map of all LootConditions registered through this API
     */
    @ApiStatus.Internal
    public Map<String, Function<String, LootCondition>> getRegisteredCustomLootConditions() {
        return Collections.unmodifiableMap(this.customRegisteredConditions);
    }

}
