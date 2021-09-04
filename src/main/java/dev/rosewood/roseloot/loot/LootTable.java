package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.item.LootItem;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LootTable implements LootItemGenerator {

    private final String name;
    private final LootTableType type;
    private final List<LootCondition> conditions;
    private final List<LootPool> pools;
    private final boolean overwriteExisting;
    private final boolean directlyToLooter;

    public LootTable(String name, LootTableType type, List<LootCondition> conditions, List<LootPool> pools, boolean overwriteExisting, boolean directlyToLooter) {
        this.name = name;
        this.type = type;
        this.conditions = conditions;
        this.pools = pools;
        this.overwriteExisting = overwriteExisting;
        this.directlyToLooter = directlyToLooter;
    }

    @Override
    public List<LootItem<?>> generate(LootContext context) {
        if (!this.check(context))
            return Collections.emptyList();
        return this.pools.stream().flatMap(x -> x.generate(context).stream()).collect(Collectors.toList());
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
     * Checks if the original loot for the given context should be overwritten
     *
     * @param context The LootContext
     * @return true if this LootTable should overwrite the existing context's loot, false otherwise
     */
    public boolean shouldOverwriteExisting(LootContext context) {
        return check(context) && this.overwriteExisting;
    }

    /**
     * Checks if the original loot for the given context should go directly to inventory
     *
     * @param context The LootContext
     * @return true if loot should go directly to looter, false otherwise
     */
    public boolean shouldGoDirectlyToLooter(LootContext context) {
        return check(context) && this.directlyToLooter;
    }

}
