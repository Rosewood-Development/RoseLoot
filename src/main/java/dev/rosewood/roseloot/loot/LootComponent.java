package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.RandomCollection;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class LootComponent implements LootContentsPopulator {

    private final List<LootCondition> conditions;
    private final List<LootItem> lootItems;

    private final NumberProvider rolls, bonusRolls;
    private final NumberProvider weight, quality;
    private final ChildrenStrategy childrenStrategy;
    private final List<LootComponent> children;

    public LootComponent(List<LootCondition> conditions, NumberProvider rolls, NumberProvider bonusRolls, NumberProvider weight, NumberProvider quality, List<LootItem> lootItems, ChildrenStrategy childrenStrategy, List<LootComponent> children) {
        this.conditions = conditions;
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
        this.weight = weight;
        this.quality = quality;
        this.lootItems = lootItems;
        this.childrenStrategy = childrenStrategy;
        this.children = children;
    }

    @Override
    public void populate(LootContext context, LootContents contents) {
        int numRolls = this.rolls.getInteger(context) + (int) Math.round(this.bonusRolls.getDouble(context) * context.getLuckLevel());
        for (int i = 0; i < numRolls; i++)
            this.populateChildren(context, contents);
    }

    private void populateChildren(LootContext context, LootContents contents) {
        contents.add(this.lootItems);

        if (this.children != null && this.childrenStrategy != null) {
            switch (this.childrenStrategy) {
                case NORMAL -> {
                    List<LootComponent> unweightedEntries = new ArrayList<>();
                    RandomCollection<LootComponent> randomEntries = new RandomCollection<>();
                    for (LootComponent child : this.children) {
                        if (child.isWeighted()) {
                            // If weighted, add to the random entries if it passes conditions
                            if (!child.check(context))
                                continue;

                            randomEntries.add(child.getWeight(context), child);
                        } else {
                            // Otherwise, generate it right away
                            unweightedEntries.add(child);
                        }
                    }

                    if (!randomEntries.isEmpty())
                        randomEntries.next().populateChildren(context, contents);

                    for (LootComponent entry : unweightedEntries)
                        if (entry.check(context))
                            entry.populateChildren(context, contents);
                }
                case SEQUENTIAL -> {
                    for (LootComponent child : this.children) {
                        if (!child.check(context))
                            break;

                        child.populateChildren(context, contents);
                    }
                }
                case FIRST_PASSING -> {
                    for (LootComponent child : this.children) {
                        if (child.check(context)) {
                            child.populateChildren(context, contents);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        List<ItemStack> items = new ArrayList<>();
        if (this.children != null)
            items.addAll(this.children.stream().flatMap(x -> x.getAllItems(context).stream()).toList());
        for (LootItem lootItem : this.lootItems)
            if (lootItem instanceof ItemLootItem itemLootItem)
                items.addAll(itemLootItem.getAllItems(context));
        return items;
    }

    @Override
    public boolean check(LootContext context) {
        return this.conditions.stream().allMatch(x -> x.check(context));
    }

    /**
     * Gets the weight of this component taking the quality into account
     *
     * @param context The LootContext
     * @return the weight of this component
     */
    public double getWeight(LootContext context) {
        return this.weight.getDouble(context) + this.quality.getDouble(context) * context.getLuckLevel();
    }

    /**
     * @return true if this component is weighted
     */
    public boolean isWeighted() {
        return this.weight != null;
    }

    /**
     * The strategy to use when evaluating a LootComponent's children
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
