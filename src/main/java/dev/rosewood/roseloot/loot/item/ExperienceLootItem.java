package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import java.util.Collections;

public class ExperienceLootItem extends LootItem {

    private final int amount;

    public ExperienceLootItem(int amount) {
        this.amount = amount;
    }

    @Override
    public LootContents generate(LootContext context) {
        return new LootContents(Collections.emptyList(), Collections.emptyList(), this.amount);
    }

}
