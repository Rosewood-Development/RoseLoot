package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.table.LootTableType;
import java.util.List;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class LootTable implements LootContentsPopulator {

    private final String name;
    private final LootTableType type;
    private final List<LootCondition> conditions;
    private final List<LootComponent> components;
    private final Set<OverwriteExisting> overwriteExisting;
    private final boolean allowRecursion;

    public LootTable(String name, LootTableType type, List<LootCondition> conditions, List<LootComponent> components, Set<OverwriteExisting> overwriteExisting, boolean allowRecursion) {
        this.name = name;
        this.type = type;
        this.conditions = conditions;
        this.components = components;
        this.overwriteExisting = overwriteExisting;
        this.allowRecursion = allowRecursion;
    }

    @Override
    public void populate(LootContext context, LootContents contents) {
        this.type.validateLootContext(context);

        context.setCurrentLootTable(this);

        for (LootComponent component : this.components) {
            if (!component.check(context))
                continue;

            component.populate(context, contents);
        }
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        return this.components.stream().flatMap(x -> x.getAllItems(context).stream()).toList();
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
     * @return the overwrite-existing values defined in the LootTable
     */
    public Set<OverwriteExisting> getOverwriteExistingValues() {
        return this.overwriteExisting;
    }

    /**
     * @return true if this LootTable allows recursion
     */
    public boolean allowsRecursion() {
        return this.allowRecursion;
    }

}
