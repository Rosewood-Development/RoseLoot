package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.util.NumberProvider;
import dev.rosewood.roseloot.util.RandomCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LootEntry implements LootItemGenerator {

    private final List<LootCondition> conditions;
    private final NumberProvider weight;
    private final NumberProvider quality;
    private final List<LootItem<?>> lootItems;
    private final ChildrenStrategy childrenStrategy;
    private final List<LootEntry> children;

    public LootEntry(List<LootCondition> conditions, NumberProvider weight, NumberProvider quality, List<LootItem<?>> lootItems, ChildrenStrategy childrenStrategy, List<LootEntry> children) {
        this.conditions = conditions;
        this.weight = weight;
        this.quality = quality;
        this.lootItems = lootItems;
        this.childrenStrategy = childrenStrategy;
        this.children = children;
    }

    @Override
    public List<LootItem<?>> generate(LootContext context) {
        if (!this.check(context))
            return Collections.emptyList();

        List<LootItem<?>> generatedItems = new ArrayList<>(this.lootItems);
        if (this.children != null && this.childrenStrategy != null) {
            switch (this.childrenStrategy) {
                case NORMAL:
                    List<LootEntry> unweightedEntries = new ArrayList<>();
                    RandomCollection<LootEntry> randomEntries = new RandomCollection<>();
                    for (LootEntry child : this.children) {
                        if (!child.check(context))
                            continue;

                        if (child.isWeighted()) {
                            // If weighted, add to the random entries
                            randomEntries.add(child.getWeight(context), child);
                        } else {
                            // Otherwise generate it right away
                            unweightedEntries.add(child);
                        }
                    }

                    if (!randomEntries.isEmpty())
                        generatedItems.addAll(randomEntries.next().generate(context));
                    generatedItems.addAll(unweightedEntries.stream().flatMap(x -> x.generate(context).stream()).collect(Collectors.toList()));
                    break;
                case SEQUENTIAL:
                    for (LootEntry child : this.children) {
                        if (!child.check(context))
                            break;

                        generatedItems.addAll(child.generate(context));
                    }
                    break;
                case FIRST_PASSING:
                    for (LootEntry child : this.children) {
                        if (child.check(context)) {
                            generatedItems.addAll(child.generate(context));
                            break;
                        }
                    }
                    break;
            }
        }

        return generatedItems;
    }

    @Override
    public boolean check(LootContext context) {
        return this.conditions.stream().allMatch(x -> x.check(context));
    }

    /**
     * Gets the weight of this entry taking the quality into account
     *
     * @param context The LootContext
     * @return the weight of this entry
     */
    public int getWeight(LootContext context) {
        return (int) Math.floor(this.weight.getInteger() + this.quality.getInteger() * context.getLuckLevel());
    }

    /**
     * @return true if this entry is weighted
     */
    public boolean isWeighted() {
        return this.weight != null;
    }

    /**
     * The strategy to use when evaluating a LootEntry's children
     */
    public enum ChildrenStrategy {
        NORMAL,        // Process as if this is a LootPool with a single roll and no bonuses
        SEQUENTIAL,    // Keep processing children until a child does not pass conditions
        FIRST_PASSING; // Keep attempting to process children until one passes conditions, then stop

        public static ChildrenStrategy fromString(String name) {
            for (ChildrenStrategy value : values())
                if (value.name().toLowerCase().equals(name))
                    return value;
            return NORMAL;
        }
    }

}
