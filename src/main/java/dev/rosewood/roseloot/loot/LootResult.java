package dev.rosewood.roseloot.loot;

public class LootResult {

    private final LootContext lootContext;
    private final LootContents lootContents;
    private final boolean overwriteExisting;

    public LootResult(LootContext lootContext, LootContents lootContents, boolean overwriteExisting) {
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

    public boolean shouldOverwriteExisting() {
        return this.overwriteExisting;
    }

}
