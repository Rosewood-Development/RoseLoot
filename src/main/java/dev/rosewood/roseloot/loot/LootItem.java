package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.util.LootUtils;
import java.util.Collections;
import org.bukkit.inventory.ItemStack;

public class LootItem implements LootGenerator {

    private final ItemStack item;
    private final String command;
    private final int min;
    private final int max;
    private final int experience;

    public LootItem(ItemStack item, int min, int max, String command, int experience) {
        this.item = item;
        this.min = min;
        this.max = max;
        this.command = command;
        this.experience = experience;
    }

    @Override
    public LootContents generate(LootContext context) {
        ItemStack generatedItem = null;
        if (this.item != null) {
            int amount = LootUtils.randomInRange(this.min, this.max);
            if (amount > 0) {
                generatedItem = this.item.clone();
                generatedItem.setAmount(amount);
            }
        }

        return new LootContents(
                generatedItem != null ? Collections.singletonList(generatedItem) : Collections.emptyList(),
                this.command != null ? Collections.singletonList(this.command) : Collections.emptyList(),
                this.experience
        );
    }

}
