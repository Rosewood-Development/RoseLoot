package dev.rosewood.roseloot.event;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import java.util.Collections;
import java.util.List;
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
    private OverwriteExisting overwriteExisting;

    public PostLootGenerateEvent(LootResult lootResult) {
        this.lootResult = lootResult;
        this.dropItems = true;
        this.dropExperience = true;
        this.triggerExtras = true;
        this.overwriteExisting = lootResult.getOverwriteExisting();
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
     * @return the OverwriteExisting result of the loot generation
     */
    @NotNull
    public OverwriteExisting getOverwriteExisting() {
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
     * Sets the OverwriteExisting state of the loot generation.
     *
     * @param overwriteExisting the new OverwriteExisting state of the loot generation
     */
    public void setOverwriteExisting(OverwriteExisting overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
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
