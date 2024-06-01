package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootTable;

public final class LootArgumentHandlers {

    public static final ArgumentHandler<LootTable> LOOT_TABLE = new LootTableArgumentHandler(RoseLoot.getInstance());

    private LootArgumentHandlers() {

    }

}
