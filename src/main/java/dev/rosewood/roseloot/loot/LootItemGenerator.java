package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.item.LootItem;
import java.util.List;

public interface LootItemGenerator {

    /**
     * Generates LootItems with the given LootContext
     *
     * @param context The LootContext
     * @return generated LootItems
     */
    List<LootItem<?>> generate(LootContext context);

    /**
     * Checks if this generator passes conditions
     *
     * @param context The LootContext
     * @return true if conditions pass, false otherwise
     */
    boolean check(LootContext context);

}
