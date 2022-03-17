package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.context.LootContext;

public class LootResult {

    private final LootContext lootContext;
    private final LootContents lootContents;
    private OverwriteExisting overwriteExisting;

    public LootResult(LootContext lootContext, LootContents lootContents, OverwriteExisting overwriteExisting) {
        this.lootContext = lootContext;
        this.lootContents = lootContents;
        this.overwriteExisting = overwriteExisting;
    }

    /**
     * @return the LootContext used to generate this LootResult
     */
    public LootContext getLootContext() {
        return this.lootContext;
    }

    /**
     * @return the LootContents generated
     */
    public LootContents getLootContents() {
        return this.lootContents;
    }

    /**
     * @return the OverwriteExisting value
     */
    public OverwriteExisting getOverwriteExisting() {
        return this.overwriteExisting;
    }

    /**
     * @param overwriteExisting the OverwriteExisting value to set
     */
    public void setOverwriteExisting(OverwriteExisting overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    /**
     * @return true if this LootTable should overwrite items, false otherwise
     */
    public boolean shouldOverwriteItems() {
        return this.overwriteExisting == OverwriteExisting.ITEMS || this.overwriteExisting == OverwriteExisting.ALL;
    }

    /**
     * @return true if this LootTable should overwrite experience, false otherwise
     */
    public boolean shouldOverwriteExperience() {
        return this.overwriteExisting == OverwriteExisting.EXPERIENCE || this.overwriteExisting == OverwriteExisting.ALL;
    }

}
