package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.List;
import java.util.stream.Collectors;

public class LootTable implements LootGenerator {

    private final LootTableType type;
    private final List<LootCondition> conditions;
    private final List<LootPool> pools;
    private final boolean overwriteExisting;

    public LootTable(LootTableType type, List<LootCondition> conditions, List<LootPool> pools, boolean overwriteExisting) {
        this.type = type;
        this.conditions = conditions;
        this.pools = pools;
        this.overwriteExisting = overwriteExisting;
    }

    @Override
    public LootContents generate(LootContext context) {
        if (!this.conditions.stream().allMatch(x -> x.check(context)))
            return LootContents.empty();
        return new LootContents(this.pools.stream().map(x -> x.generate(context)).collect(Collectors.toList()));
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
        return this.conditions.stream().allMatch(x -> x.check(context)) && this.overwriteExisting;
    }

}
