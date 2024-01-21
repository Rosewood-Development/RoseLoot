package dev.rosewood.roseloot.listener.helper;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.table.LootTableType;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Arrays;

public abstract class LazyLootTableListener extends LazyListener {

    protected static final LootTableManager LOOT_TABLE_MANAGER = RoseLoot.getInstance().getManager(LootTableManager.class);

    public LazyLootTableListener(RosePlugin rosePlugin, LootTableType... lootTableTypes) {
        super(rosePlugin, () -> LOOT_TABLE_MANAGER.isLootTableTypeUsed(Arrays.asList(lootTableTypes)));
    }

}
