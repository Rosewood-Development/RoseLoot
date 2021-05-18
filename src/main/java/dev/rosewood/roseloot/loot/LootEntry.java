package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.List;
import java.util.stream.Collectors;

public class LootEntry implements LootGenerator {

    private final List<LootCondition> conditions;
    private final int weight;
    private final int quality;
    private final List<LootItem> lootItems;

    public LootEntry(List<LootCondition> conditions, int weight, int quality, List<LootItem> lootItems) {
        this.conditions = conditions;
        this.weight = weight;
        this.quality = quality;
        this.lootItems = lootItems;
    }

    @Override
    public LootContents generate(LootContext context) {
        if (!this.conditions.stream().allMatch(x -> x.check(context)))
            return LootContents.empty();
        return new LootContents(this.lootItems.stream().map(x -> x.generate(context)).collect(Collectors.toList()));
    }

    /**
     * Gets the weight of this entry taking the quality into account
     *
     * @param context The LootContext
     * @return the weight of this entry
     */
    public int getWeight(LootContext context) {
        return (int) Math.floor(this.weight + this.quality * context.getLuckLevel());
    }

}
