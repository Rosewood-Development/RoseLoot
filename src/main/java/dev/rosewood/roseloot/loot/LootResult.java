package dev.rosewood.roseloot.loot;

public class LootResult {

    private final LootContext lootContext;
    private final LootContents lootContents;
    private final boolean overwriteExisting;
    private final boolean directlyToLooter;

    public LootResult(LootContext lootContext, LootContents lootContents, boolean overwriteExisting, boolean directlyToLooter) {
        this.lootContext = lootContext;
        this.lootContents = lootContents;
        this.overwriteExisting = overwriteExisting;
        this.directlyToLooter = directlyToLooter;
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

    public boolean shouldGoDirectlyToLooter() {
        return directlyToLooter;
    }
}
