package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.configuration.ConfigurationSection;

public class ExperienceLootItem implements LootItem<Integer> {

    private int min;
    private int max;

    public ExperienceLootItem(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer create(LootContext context) {
        return LootUtils.randomInRange(this.min, this.max);
    }

    @Override
    public boolean combineWith(LootItem<?> lootItem) {
        if (!(lootItem instanceof ExperienceLootItem))
            return false;

        ExperienceLootItem other = (ExperienceLootItem) lootItem;
        this.min += other.min;
        this.max += other.max;
        return true;
    }

    public static LootItem<?> fromSection(ConfigurationSection section) {
        int minExp, maxExp;
        if (section.contains("amount")) {
            minExp = maxExp = section.getInt("amount");
        } else {
            minExp = section.getInt("min", 1);
            maxExp = section.getInt("max", 1);
        }

        return new ExperienceLootItem(minExp, maxExp);
    }

}
