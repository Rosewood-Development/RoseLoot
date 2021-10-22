package dev.rosewood.roseloot.loot;

public class LootResult {

    private final LootContext lootContext;
    private final LootContents lootContents;
    private final OverwriteExisting overwriteExisting;

    public LootResult(LootContext lootContext, LootContents lootContents, OverwriteExisting overwriteExisting) {
        this.lootContext = lootContext;
        this.lootContents = lootContents;
        this.overwriteExisting = overwriteExisting;
    }

    public LootContext getLootContext() {
        return this.lootContext;
    }

    public LootContents getLootContents() {
        return this.lootContents;
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
