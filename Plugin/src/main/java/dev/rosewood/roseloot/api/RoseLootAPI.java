package dev.rosewood.roseloot.api;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.event.LootItemTypeRegistrationEvent;
import dev.rosewood.roseloot.event.LootTableTypeRegistrationEvent;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.table.LootTableType;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class RoseLootAPI {

    private static RoseLootAPI instance;

    private final Map<String, Function<String, LootCondition>> registeredConditions;
    private final Map<String, Function<ConfigurationSection, LootItem>> registeredLootItemTypes;
    private final Map<String, LootTableType> registeredLootTableTypes;

    private LootTableManager lootTableManager;

    private RoseLootAPI() {
        this.registeredConditions = new HashMap<>();
        this.registeredLootItemTypes = new HashMap<>();
        this.registeredLootTableTypes = new HashMap<>();
    }

    public static RoseLootAPI getInstance() {
        if (instance == null)
            instance = new RoseLootAPI();
        return instance;
    }

    private LootTableManager getLootTableManager() {
        if (this.lootTableManager == null)
            this.lootTableManager = RoseLoot.getInstance().getManager(LootTableManager.class);
        return this.lootTableManager;
    }

    /**
     * Generates and returns a LootResult that contains the results of the loot generation.
     * This method takes a LootTableType (default types available in {@link LootTableTypes}) and a LootContext
     * (builder available at {@link LootContext#builder()}) and runs all registered loot tables for that type that pass
     * conditions. Does not generate vanilla loot.
     *
     * @param lootTableType The LootTableType to generate loot for
     * @param context The LootContext used for loot generation
     * @return a LootResult containing the results of the loot generation
     */
    public LootResult getLoot(LootTableType lootTableType, LootContext context) {
        return this.getLootTableManager().getLoot(lootTableType, context);
    }

    /**
     * Generates and returns a LootResult that contains the results of the loot generation.
     * This method takes a LootTable and a LootContext (builder available at {@link LootContext#builder()}) and runs all
     * registered loot tables for that type that pass conditions. Does not generate vanilla loot.
     *
     * @param lootTable The LootTable to use to generate loot
     * @param context The LootContext used for loot generation
     * @return a LootResult containing the results of the loot generation
     */
    public LootResult getLoot(LootTable lootTable, LootContext context) {
        return this.getLootTableManager().getLoot(lootTable, context);
    }

    /**
     * Gets a LootTable by its LootTableType and name.
     *
     * @param lootTableType The LootTableType of the loot table
     * @param name The fully qualified name of the loot table
     * @return a loot table, or null
     */
    public LootTable getLootTable(LootTableType lootTableType, String name) {
        return this.getLootTableManager().getLootTable(lootTableType, name);
    }

    /**
     * Gets a LootTable by its name.
     *
     * @param name The fully qualified name of the loot table
     * @return a loot table, or null
     */
    public LootTable getLootTable(String name) {
        return this.getLootTableManager().getLootTable(name);
    }

    /**
     * Registers a custom LootCondition with the given prefix and tag parser.
     * This LootCondition will automatically be loaded when RoseLoot enables or reloads.
     *
     * @param name The prefix of the condition (ex. `killed-by`)
     * @param parser The parser for the condition.
     *               The function parser should fulfill the following contract:
     *               - The argument is the unparsed tag entered as a condition (ex. `killed-by:player`)
     *               - The function should accept and parse any condition tag that starts with the given name
     *               - The function should return a LootCondition that will be used to check the condition
     *               - The function should return null or throw an IllegalArgumentException if the tag is invalid
     * @return true if registering the new LootCondition overwrote a different LootCondition with the same name, false otherwise
     * @see LootConditionRegistrationEvent for an alternative registration method
     */
    public boolean registerCustomLootCondition(String name, Function<String, LootCondition> parser) {
        return this.registeredConditions.put(name, parser) != null;
    }

    /**
     * Unregisters a custom LootCondition.
     *
     * @param name The name of the LootCondition to unregister
     * @return true if a LootCondition was unregistered, false otherwise
     */
    public boolean unregisterCustomLootCondition(String name) {
        return this.registeredConditions.remove(name) != null;
    }

    /**
     * Registers a custom LootItem type, overwriting any existing LootItems with the same name.
     * This LootItem type will automatically be loaded when RoseLoot enables or reloads.
     *
     * @param name The name of the LootItem type to register
     * @param function The function to read a ConfigurationSection and output a LootItem
     * @return true if registering the new LootItem type overwrote a different LootItem type with the same name, false otherwise
     * @see LootItemTypeRegistrationEvent for an alternative registration method
     */
    public boolean registerCustomLootItem(@NotNull String name, @NotNull Function<ConfigurationSection, LootItem> function) {
        return this.registeredLootItemTypes.put(name.toLowerCase(), function) == null;
    }

    /**
     * Unregisters a custom LootItem type
     *
     * @param name The name of the LootItem type to unregister
     * @return true if a LootItem type was unregistered, false otherwise
     */
    public boolean unregisterCustomLootItem(@NotNull String name) {
        return this.registeredLootItemTypes.remove(name.toLowerCase()) != null;
    }

    /**
     * Registers a custom LootTableType, overwriting any existing LootTableType with the same name.
     * This LootTableType will automatically be loaded when RoseLoot enables or reloads.
     *
     * @param name The name of the LootTableType to register
     * @return true if registering the new LootTableType overwrote a different LootTableType with the same name, false otherwise
     * @see LootTableTypeRegistrationEvent for an alternative registration method
     */
    public boolean registerLootTableType(@NotNull String name, @NotNull LootTableType lootTableType) {
        return this.registeredLootTableTypes.put(name.toLowerCase(), lootTableType) == null;
    }

    /**
     * Unregisters a custom LootItem type
     *
     * @param name The name of the LootItem type to unregister
     * @return true if a LootItem type was unregistered, false otherwise
     */
    public boolean unregisterLootTableType(@NotNull String name) {
        return this.registeredLootTableTypes.remove(name.toLowerCase()) != null;
    }

    /**
     * @return An unmodifiable map of all LootConditions registered through this API
     */
    @ApiStatus.Internal
    public Map<String, Function<String, LootCondition>> getRegisteredCustomLootConditions() {
        return Collections.unmodifiableMap(this.registeredConditions);
    }

    /**
     * @return An unmodifiable map of all LootItem types registered through this API
     */
    @ApiStatus.Internal
    public Map<String, Function<ConfigurationSection, LootItem>> getRegisteredCustomLootItemTypes() {
        return Collections.unmodifiableMap(this.registeredLootItemTypes);
    }

    /**
     * @return An unmodifiable map of all LootTableTypes registered through this API
     */
    @ApiStatus.Internal
    public Map<String, LootTableType> getRegisteredCustomLootTableTypes() {
        return Collections.unmodifiableMap(this.registeredLootTableTypes);
    }

}
