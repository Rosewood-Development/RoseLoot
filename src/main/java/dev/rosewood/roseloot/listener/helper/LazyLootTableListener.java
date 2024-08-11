package dev.rosewood.roseloot.listener.helper;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.table.LootTableType;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Arrays;

public abstract class LazyLootTableListener extends LazyListener {

    public LazyLootTableListener(RosePlugin rosePlugin, LootTableType... lootTableTypes) {
        super(rosePlugin, () -> rosePlugin.getManager(LootTableManager.class).isLootTableTypeUsed(Arrays.asList(lootTableTypes)));
    }

}
