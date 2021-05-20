package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemLootItem extends LootItem {

    private final Material item;
    private final int min;
    private final int max;

    public ItemLootItem(Material item, int min, int max) {
        this.item = item;
        this.min = min;
        this.max = max;
    }

    @Override
    public LootContents generate(LootContext context) {
        ItemStack generatedItem = null;
        int amount = LootUtils.randomInRange(this.min, this.max);
        if (amount > 0)
            generatedItem = new ItemStack(this.item, amount);

        return new LootContents(Collections.singletonList(generatedItem), Collections.emptyList(), 0);
    }

}
