package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.RandomCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LootPool implements LootGenerator {

    private final List<LootCondition> conditions;
    private final int rolls;
    private final int bonusRolls;
    private final List<LootEntry> entries;

    public LootPool(List<LootCondition> conditions, int rolls, int bonusRolls, List<LootEntry> entries) {
        this.conditions = conditions;
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
        this.entries = entries;
    }

    @Override
    public LootContents generate(LootContext context) {
        if (!this.conditions.stream().allMatch(x -> x.check(context)))
            return LootContents.empty();

        if (this.entries.size() == 1)
            return this.entries.get(0).generate(context);

        List<LootContents> lootContents = new ArrayList<>();
        List<LootEntry> unweightedEntries = new ArrayList<>();
        RandomCollection<LootEntry> randomEntries = new RandomCollection<>();
        for (LootEntry entry : this.entries) {
            int weight = entry.getWeight(context);
            if (weight > 0) {
                // If weighted, add to the random entries
                randomEntries.add(weight, entry);
            } else {
                // Otherwise generate it right away
                unweightedEntries.add(entry);
            }
        }

        int numRolls = this.rolls + this.bonusRolls * context.getLuckLevel();
        for (int i = 0; i < numRolls; i++) {
            if (!randomEntries.isEmpty())
                lootContents.add(randomEntries.next().generate(context));
            lootContents.addAll(unweightedEntries.stream().map(x -> x.generate(context)).collect(Collectors.toList()));
        }

        return new LootContents(lootContents);
    }

}
