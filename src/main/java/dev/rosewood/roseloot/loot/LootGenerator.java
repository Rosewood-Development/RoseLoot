package dev.rosewood.roseloot.loot;

public interface LootGenerator {

    /**
     * Generates loot with the given LootContext
     *
     * @param context The LootContext
     * @return generated loot
     */
    LootContents generate(LootContext context);

    /**
     * Checks if this generator passes conditions
     *
     * @param context The LootContext
     * @return true if conditions pass, false otherwise
     */
    boolean check(LootContext context);

}
