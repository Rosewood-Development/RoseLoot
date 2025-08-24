package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootTable;
import org.bukkit.World;

public final class LootArgumentHandlers {

    public static final ArgumentHandler<LootTable> LOOT_TABLE = new LootTableArgumentHandler(RoseLoot.getInstance());
    public static final ArgumentHandler<World> WORLD = new WorldArgumentHandler();

    private LootArgumentHandlers() {

    }

}
