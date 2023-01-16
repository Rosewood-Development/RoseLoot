package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public interface LootContentsPopulator {

    /**
     * Populates the LootContext with the contents of this LootContentsPopulator
     *
     * @param context The LootContext
     * @param contents The LootContents to populate
     */
    void populate(LootContext context, LootContents contents);

    /**
     * Gets all items that can be generated from this LootContentsPopulator
     *
     * @param context The LootContext
     * @return all items that can possibly be generated
     */
    List<ItemStack> getAllItems(LootContext context);

    /**
     * Checks if this populator passes conditions
     *
     * @param context The LootContext
     * @return true if conditions pass, false otherwise
     */
    boolean check(LootContext context);

}
