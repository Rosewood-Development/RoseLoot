package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class LootResult {

    private final LootContext lootContext;
    private final LootContents lootContents;
    private Set<OverwriteExisting> overwriteExisting;

    public LootResult(LootContext lootContext, LootContents lootContents, Set<OverwriteExisting> overwriteExisting) {
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
     * @return the OverwriteExisting values
     */
    public Set<OverwriteExisting> getOverwriteExistingValues() {
        return this.overwriteExisting;
    }

    /**
     * @param overwriteExisting the OverwriteExisting values to set
     */
    public void setOverwriteExistingValues(Collection<OverwriteExisting> overwriteExisting) {
        this.overwriteExisting = EnumSet.copyOf(overwriteExisting);
    }

    /**
     * Checks if something with existing values should be overwritten
     *
     * @param type the type of existing value
     * @return true if the existing value should be overwritten
     */
    public boolean doesOverwriteExisting(OverwriteExisting type) {
        return this.overwriteExisting.contains(type);
    }

}
