package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import java.util.Collections;

public class CommandLootItem extends LootItem {

    private final String command;

    public CommandLootItem(String command) {
        this.command = command;
    }

    @Override
    public LootContents generate(LootContext context) {
        return new LootContents(Collections.emptyList(), Collections.singletonList(this.command), 0);
    }

}
