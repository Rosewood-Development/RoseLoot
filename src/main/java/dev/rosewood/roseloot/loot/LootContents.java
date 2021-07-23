package dev.rosewood.roseloot.loot;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.item.ExplosionLootItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Holds miscellaneous contents that can be obtained from looting
 */
public class LootContents {

    private List<ItemStack> items;
    private List<String> commands;
    private int experience;
    private ExplosionLootItem.ExplosionState explosionState;

    private LootContents() {

    }

    private LootContents(List<LootContents> results) {
        this.items = new ArrayList<>();
        this.commands = new ArrayList<>();

        int experience = 0;
        ExplosionLootItem.ExplosionState explosionState = null;
        for (LootContents result : results) {
            this.items.addAll(result.getItems());
            this.commands.addAll(result.getCommands());
            experience += result.getExperience();
            if (explosionState == null) {
                explosionState = result.getExplosionState();
            } else if (result.getExplosionState() != null) {
                explosionState = ExplosionLootItem.ExplosionState.combine(explosionState, result.getExplosionState());
            }
        }

        this.experience = experience;
        this.explosionState = explosionState;
    }

    /**
     * @return the items of this loot contents
     */
    public List<ItemStack> getItems() {
        return this.items == null ? Collections.emptyList() : Collections.unmodifiableList(this.items);
    }

    /**
     * @return the commands of this loot contents
     */
    public List<String> getCommands() {
        return this.commands == null ? Collections.emptyList() : Collections.unmodifiableList(this.commands);
    }

    /**
     * @return the experience amount of this loot contents
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * @return the highest explosion power level of this loot contents
     */
    public ExplosionLootItem.ExplosionState getExplosionState() {
        return this.explosionState;
    }

    /**
     * Triggers the execution for anything that isn't an item or experience drop
     *
     * @param player The Player to use for running commands
     * @param location The Location to execute the rest of the drops at
     */
    public void triggerExtras(Player player, Location location) {
        World world = location.getWorld();
        if (world == null)
            return;

        // Trigger explosion if applicable
        if (this.explosionState != null)
            this.explosionState.trigger(location);

        // Run commands
        if (!this.commands.isEmpty()) {
            StringPlaceholders.Builder stringPlaceholdersBuilder = StringPlaceholders.builder("world", world.getName())
                    .addPlaceholder("x", location.getX())
                    .addPlaceholder("y", location.getY())
                    .addPlaceholder("z", location.getZ());

            boolean isPlayer = player != null;
            if (isPlayer)
                stringPlaceholdersBuilder.addPlaceholder("player", player.getName());

            StringPlaceholders stringPlaceholders = stringPlaceholdersBuilder.build();
            for (String command : this.commands)
                if (!command.contains("%player%") || isPlayer)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stringPlaceholders.apply(command));
        }
    }

    /**
     * @return an empty LootContents
     */
    public static LootContents empty() {
        return new LootContents(Collections.emptyList());
    }

    /**
     * Creates a new LootContents instance from existing LootContents
     *
     * @param existing The existing LootContents
     * @return a new LootContents instance holding all existing values
     */
    public static LootContents ofExisting(List<LootContents> existing) {
        return new LootContents(existing);
    }

    /**
     * Creates a new LootContents instance of ItemStacks
     *
     * @param items The ItemStacks
     * @return a new LootContents instance of ItemStacks
     */
    public static LootContents ofItems(List<ItemStack> items) {
        LootContents contents = new LootContents();
        contents.items = items;
        return contents;
    }

    /**
     * Creates a new LootContents instance of commands
     *
     * @param commands The commands
     * @return a new LootContents instance of commands
     */
    public static LootContents ofCommands(List<String> commands) {
        LootContents contents = new LootContents();
        contents.commands = commands;
        return contents;
    }

    /**
     * Creates a new LootContents instance of experience
     *
     * @param experience The experience
     * @return a new LootContents instance of experience
     */
    public static LootContents ofExperience(int experience) {
        LootContents contents = new LootContents();
        contents.experience = experience;
        return contents;
    }

    /**
     * Creates a new LootContents instance of an explosion
     *
     * @param explosionState The ExplosionState
     * @return a new LootContents instance of an explosion
     */
    public static LootContents ofExplosion(ExplosionLootItem.ExplosionState explosionState) {
        LootContents contents = new LootContents();
        contents.explosionState = explosionState;
        return contents;
    }

}
