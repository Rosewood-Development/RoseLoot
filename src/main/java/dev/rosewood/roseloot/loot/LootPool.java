package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.RandomCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class LootPool implements LootContentsPopulator {

    private final List<LootCondition> conditions;
    private final NumberProvider rolls, bonusRolls;
    private final List<LootEntry> entries;

    public LootPool(List<LootCondition> conditions, NumberProvider rolls, NumberProvider bonusRolls, List<LootEntry> entries) {
        this.conditions = conditions;
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
        this.entries = entries;
    }

    @Override
    public void populate(LootContext context, LootContents contents) {
        if (!this.check(context))
            return;

        List<LootEntry> unweightedEntries = new ArrayList<>();
        RandomCollection<LootEntry> randomEntries = new RandomCollection<>();
        for (LootEntry entry : this.entries) {
            if (entry.isWeighted()) {
                // If weighted, add to the random entries if it passes conditions
                if (!entry.check(context))
                    continue;

                randomEntries.add(entry.getWeight(context), entry);
            } else {
                // Otherwise, generate it right away
                unweightedEntries.add(entry);
            }
        }

        Map<LootEntry, Boolean> checkedConditions = new HashMap<>();
        int numRolls = this.rolls.getInteger(context) + (int) Math.round(this.bonusRolls.getDouble(context) * context.getLuckLevel());
        for (int i = 0; i < numRolls; i++) {
            if (!randomEntries.isEmpty())
                randomEntries.next().populate(context, contents);

            for (LootEntry entry : unweightedEntries)
                if (checkedConditions.computeIfAbsent(entry, x -> x.check(context)))
                    entry.populate(context, contents);
        }
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        return this.entries.stream().flatMap(x -> x.getAllItems(context).stream()).toList();
    }

    @Override
    public boolean check(LootContext context) {
        return this.conditions.stream().allMatch(x -> x.check(context));
    }

}
