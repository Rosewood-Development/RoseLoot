package dev.rosewood.roseloot.event;

import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that gets called after RoseLoot has finished generating loot and is about to drop it.
 * This event is only meant to get and/or disable certain loot types being dropped, not modify it.
 */
public class PostLootGenerateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final LootResult lootResult;
    private boolean cancelled;
    private boolean dropItems, dropExperience, triggerExtras;
    private Set<OverwriteExisting> overwriteExisting;

    public PostLootGenerateEvent(LootResult lootResult) {
        super(!Bukkit.isPrimaryThread());
        this.lootResult = lootResult;
        this.dropItems = true;
        this.dropExperience = true;
        this.triggerExtras = true;
        this.overwriteExisting = lootResult.getOverwriteExistingValues();
    }

    /**
     * @return the LootContext used to generate this loot
     */
    @NotNull
    public LootContext getLootContext() {
        return this.lootResult.getLootContext();
    }

    /**
     * @return an unmodifiable list of ItemStacks that were generated
     */
    @NotNull
    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(this.lootResult.getLootContents().getItems());
    }

    /**
     * @return the amount of experience that was generated
     */
    public int getExperience() {
        return this.lootResult.getLootContents().getExperience();
    }

    /**
     * @return true if there are other actions that will happen due to the loot generation, false otherwise
     */
    public boolean hasExtraTriggers() {
        return this.lootResult.getLootContents().hasExtraTriggers();
    }

    /**
     * @return the OverwriteExisting values of the loot generation
     */
    @NotNull
    public Set<OverwriteExisting> getOverwriteExistingValues() {
        return this.overwriteExisting;
    }

    /**
     * @return true if the loot generation should drop items, false otherwise
     */
    public boolean shouldDropItems() {
        return this.dropItems;
    }

    /**
     * @return true if the loot generation should drop experience, false otherwise
     */
    public boolean shouldDropExperience() {
        return this.dropExperience;
    }

    /**
     * @return true if the loot generation should trigger any extra actions, false otherwise
     */
    public boolean shouldTriggerExtras() {
        return this.triggerExtras;
    }

    /**
     * Sets the OverwriteExisting values for the loot generation.
     *
     * @param overwriteExisting the new OverwriteExisting values for the loot generation
     */
    public void setOverwriteExistingValues(Collection<OverwriteExisting> overwriteExisting) {
        this.overwriteExisting = EnumSet.copyOf(overwriteExisting);
    }

    /**
     * Sets if the loot generation should drop items.
     *
     * @param dropItems true if the loot generation should drop items, false otherwise
     */
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    /**
     * Sets if the loot generation should drop experience.
     *
     * @param dropExperience true if the loot generation should drop experience, false otherwise
     */
    public void setDropExperience(boolean dropExperience) {
        this.dropExperience = dropExperience;
    }

    /**
     * Sets if the loot generation should trigger any extra actions.
     *
     * @param triggerExtras true if the loot generation should trigger any extra actions, false otherwise
     */
    public void setTriggerExtras(boolean triggerExtras) {
        this.triggerExtras = triggerExtras;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
