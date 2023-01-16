package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.table.LootTableType;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class LootTable implements LootContentsPopulator {

    private final String name;
    private final LootTableType type;
    private final List<LootCondition> conditions;
    private final List<LootPool> pools;
    private final OverwriteExisting overwriteExisting;
    private final boolean allowRecursion;

    public LootTable(String name, LootTableType type, List<LootCondition> conditions, List<LootPool> pools, OverwriteExisting overwriteExisting, boolean allowRecursion) {
        this.name = name;
        this.type = type;
        this.conditions = conditions;
        this.pools = pools;
        this.overwriteExisting = overwriteExisting;
        this.allowRecursion = allowRecursion;
    }

    @Override
    public void populate(LootContext context, LootContents contents) {
        this.populate(context, contents, false);
    }

    public void populate(LootContext context, LootContents contents, boolean ignoreChecks) {
        this.type.validateLootContext(context);

        context.setCurrentLootTable(this);

        if (!ignoreChecks && !this.check(context))
            return;

        this.pools.forEach(x -> x.populate(context, contents));
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        return this.pools.stream().flatMap(x -> x.getAllItems(context).stream()).toList();
    }

    @Override
    public boolean check(LootContext context) {
        return this.conditions.stream().allMatch(x -> x.check(context));
    }

    /**
     * @return the name of this LootTable
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the type of this LootTable
     */
    public LootTableType getType() {
        return this.type;
    }

    /**
     * @return the overwrite-existing value defined in the LootTable
     */
    public OverwriteExisting getOverwriteExistingValue() {
        return this.overwriteExisting;
    }

    /**
     * @return true if this LootTable allows recursion
     */
    public boolean allowsRecursion() {
        return this.allowRecursion;
    }

}
