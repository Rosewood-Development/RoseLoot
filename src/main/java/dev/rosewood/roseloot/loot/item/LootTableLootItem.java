package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.LootTableManager;

public class LootTableLootItem extends LootItem {

    private final String lootTableName;
    private boolean invalid;
    private LootTable lootTable;
    private boolean running;

    public LootTableLootItem(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    @Override
    public LootContents generate(LootContext context) {
        if (this.invalid)
            return LootContents.empty();

        if (this.lootTable == null) {
            RosePlugin rosePlugin = RoseLoot.getInstance();
            this.lootTable = rosePlugin.getManager(LootTableManager.class).getLootTable(LootTableType.LOOT_TABLE, this.lootTableName);
            if (this.lootTable == null) {
                this.invalid = true;
                rosePlugin.getLogger().warning("Could not find loot table specified: " + this.lootTableName);
                return LootContents.empty();
            }
        }

        if (this.running) {
            RoseLoot.getInstance().getLogger().severe("Detected and blocked potential infinite recursion for loot table: " + this.lootTableName + ". " +
                    "This loot table will be empty and log this error message until fixed.");
            this.running = false;
            return LootContents.empty();
        }

        this.running = true;
        LootContents contents = this.lootTable.generate(context);
        this.running = false;
        return contents;
    }

}
