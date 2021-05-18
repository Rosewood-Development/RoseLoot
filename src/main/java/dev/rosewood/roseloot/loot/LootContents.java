package dev.rosewood.roseloot.loot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class LootContents {

    private final List<ItemStack> items;
    private final List<String> commands;
    private final int experience;

    public LootContents(List<ItemStack> items, List<String> commands, int experience) {
        this.items = items == null ? Collections.emptyList() : items;
        this.commands = commands == null ? Collections.emptyList() : commands;
        this.experience = experience;
    }

    public LootContents(List<LootContents> results) {
        this.items = new ArrayList<>();
        this.commands = new ArrayList<>();

        int experience = 0;
        for (LootContents result : results) {
            this.items.addAll(result.getItems());
            this.commands.addAll(result.getCommands());
            experience += result.getExperience();
        }

        this.experience = experience;
    }

    /**
     * @return the items of this loot contents
     */
    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * @return the commands of this loot contents
     */
    public List<String> getCommands() {
        return Collections.unmodifiableList(this.commands);
    }

    /**
     * @return the experience amount of this loot contents
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * @return an empty LootContents
     */
    public static LootContents empty() {
        return new LootContents(Collections.emptyList());
    }

}
