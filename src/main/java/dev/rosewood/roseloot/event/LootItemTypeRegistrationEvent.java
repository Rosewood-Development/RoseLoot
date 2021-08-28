package dev.rosewood.roseloot.event;

import dev.rosewood.roseloot.loot.item.LootItem;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that gets called during the RoseLoot LootItem registration
 */
public class LootItemTypeRegistrationEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Map<String, Function<ConfigurationSection, LootItem<?>>> registeredLootItemsTypes;

    public LootItemTypeRegistrationEvent() {
        this.registeredLootItemsTypes = new HashMap<>();
    }

    /**
     * @return an unmodifiable map of registered LootItem types
     */
    public Map<String, Function<ConfigurationSection, LootItem<?>>> getRegisteredLootItemsTypes() {
        return Collections.unmodifiableMap(this.registeredLootItemsTypes);
    }

    /**
     * Registers a LootItem type, overwriting any existing LootItems with the same name
     *
     * @param name The name of the LootItem type to register
     * @param function The function to read a ConfigurationSection and output a LootItem
     * @return true if registering the new LootItem type overwrote a different LootItem type with the same name, false otherwise
     */
    public boolean registerLootItem(String name, Function<ConfigurationSection, LootItem<?>> function) {
        return this.registeredLootItemsTypes.put(name.toUpperCase(), function) == null;
    }

    /**
     * Unregisters a LootItem type
     *
     * @param name The name of the LootItem type to unregister
     * @return true if a LootItem type was unregistered, false otherwise
     */
    public boolean unregisterLootItem(String name) {
        return this.registeredLootItemsTypes.remove(name.toUpperCase()) != null;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
