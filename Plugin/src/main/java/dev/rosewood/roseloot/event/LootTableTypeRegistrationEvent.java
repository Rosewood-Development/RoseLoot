package dev.rosewood.roseloot.event;

import dev.rosewood.roseloot.loot.table.LootTableType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event that gets called during the RoseLoot LootTableType registration
 */
public class LootTableTypeRegistrationEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Map<String, LootTableType> registeredLootTableTypes;

    public LootTableTypeRegistrationEvent() {
        this.registeredLootTableTypes = new HashMap<>();
    }

    /**
     * @return an unmodifiable map of registered LootTableTypes
     */
    @NotNull
    public Map<String, LootTableType> getRegisteredLootTableTypes() {
        return Collections.unmodifiableMap(this.registeredLootTableTypes);
    }

    /**
     * Registers a LootTableType, overwriting any existing LootTableType with the same name
     *
     * @param name The name of the LootTableType to register
     * @return true if registering the new LootTableType overwrote a different LootTableType with the same name, false otherwise
     */
    public boolean registerLootTableType(@NotNull String name, @NotNull LootTableType lootTableType) {
        return this.registeredLootTableTypes.put(name.toLowerCase(), lootTableType) == null;
    }

    /**
     * Unregisters a LootItem type
     *
     * @param name The name of the LootItem type to unregister
     * @return true if a LootItem type was unregistered, false otherwise
     */
    public boolean unregisterLootTableType(@NotNull String name) {
        return this.registeredLootTableTypes.remove(name.toLowerCase()) != null;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
