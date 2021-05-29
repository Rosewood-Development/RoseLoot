package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;

public class ExperienceLootItem extends LootItem {

    private final int min;
    private final int max;

    public ExperienceLootItem(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public LootContents generate(LootContext context) {
        return LootContents.ofExperience(LootUtils.randomInRange(this.min, this.max));
    }

}
