package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public interface LootItemGenerator<T> {

    /**
     * Generates contents with the given LootContext
     *
     * @param context The LootContext
     * @return generated contents
     */
    List<T> generate(LootContext context);

    /**
     * Gets all items that can be generated from this LootItemGenerator
     *
     * @param context The LootContext
     * @return all items that can possibly be generated
     */
    List<ItemStack> getAllItems(LootContext context);

}
