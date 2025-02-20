package dev.rosewood.roseloot.event;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event that gets called during the RoseLoot LootCondition registration
 */
public class LootConditionRegistrationEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Map<String, Function<String, LootCondition>> registeredConditions;

    public LootConditionRegistrationEvent() {
        this.registeredConditions = new HashMap<>();
    }

    /**
     * Registers a LootCondtion by its Class, overwriting any existing LootConditions with the same name.
     * The given class must have a constructor accepting a single String.
     *
     * @param name The name of the LootCondition to register
     * @return true if registering the new LootCondition overwrote a different LootCondition with the same name, false otherwise
     */
    public boolean registerLootCondition(@NotNull String name, @NotNull Function<String, LootCondition> lootConditionFactory) {
        return this.registeredConditions.put(name.toLowerCase(), lootConditionFactory) != null;
    }

    /**
     * Unregisters a LootCondition
     *
     * @param name The name of the LootCondition to unregister
     * @return true if a LootCondition was unregistered, false otherwise
     */
    public boolean unregisterLootCondition(@NotNull String name) {
        return this.registeredConditions.remove(name.toLowerCase()) != null;
    }

    /**
     * @return A map of all registered LootConditions
     */
    public Map<String, Function<String, LootCondition>> getRegisteredConditions() {
        return Collections.unmodifiableMap(this.registeredConditions);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
