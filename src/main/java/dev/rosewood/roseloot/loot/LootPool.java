package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.util.NumberProvider;
import dev.rosewood.roseloot.util.RandomCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;

public class LootPool implements CheckedLootItemGenerator {

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
    public List<LootItem> generate(LootContext context) {
        if (!this.check(context))
            return List.of();

        List<LootItem> lootItems = new ArrayList<>();
        List<LootEntry> unweightedEntries = new ArrayList<>();
        RandomCollection<LootEntry> randomEntries = new RandomCollection<>();
        for (LootEntry entry : this.entries) {
            if (!entry.check(context))
                continue;

            if (entry.isWeighted()) {
                // If weighted, add to the random entries
                randomEntries.add(entry.getWeight(context), entry);
            } else {
                // Otherwise, generate it right away
                unweightedEntries.add(entry);
            }
        }

        int numRolls = this.rolls.getInteger() + (int) Math.round(this.bonusRolls.getDouble() * context.getLuckLevel());
        for (int i = 0; i < numRolls; i++) {
            if (!randomEntries.isEmpty())
                lootItems.addAll(randomEntries.next().generate(context));
            lootItems.addAll(unweightedEntries.stream().flatMap(x -> x.generate(context).stream()).collect(Collectors.toList()));
        }

        return lootItems;
    }

    @Override
    public List<ItemStack> getAllItems() {
        return this.entries.stream().map(CheckedLootItemGenerator::getAllItems)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public boolean check(LootContext context) {
        return this.conditions.stream().allMatch(x -> x.check(context));
    }

}
