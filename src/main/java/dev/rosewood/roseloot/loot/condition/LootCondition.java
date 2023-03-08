package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.context.LootContext;

public interface LootCondition {

    /**
     * Checks if the LootContext meets this tag's condition
     *
     * @param context The LootContext
     * @return true if the condition is met, otherwise false
     */
    boolean check(LootContext context);

}
