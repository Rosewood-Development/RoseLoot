package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.table.LootTableType;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;

public class LootTable implements CheckedLootItemGenerator {

    private final String name;
    private final LootTableType type;
    private final List<LootCondition> conditions;
    private final List<LootPool> pools;
    private final OverwriteExisting overwriteExisting;

    public LootTable(String name, LootTableType type, List<LootCondition> conditions, List<LootPool> pools, OverwriteExisting overwriteExisting) {
        this.name = name;
        this.type = type;
        this.conditions = conditions;
        this.pools = pools;
        this.overwriteExisting = overwriteExisting;
    }

    @Override
    public List<LootItem> generate(LootContext context) {
        return this.generate(context, false);
    }

    public List<LootItem> generate(LootContext context, boolean ignoreChecks) {
        this.type.validateLootContext(context);

        if (!ignoreChecks && !this.check(context))
            return List.of();

        return this.pools.stream().flatMap(x -> x.generate(context).stream()).collect(Collectors.toList());
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        return this.pools.stream().flatMap(x -> x.getAllItems(context).stream()).collect(Collectors.toList());
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

}
