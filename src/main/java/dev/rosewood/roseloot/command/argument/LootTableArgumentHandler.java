package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LootTableArgumentHandler extends RoseCommandArgumentHandler<LootTable> {

    public LootTableArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, LootTable.class);
    }

    @Override
    protected LootTable handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        LootTable value = this.rosePlugin.getManager(LootTableManager.class).getLootTable(input);
        if (value == null)
            throw new HandledArgumentException("LootTable [" + input + "] does not exist");
        return value;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        List<LootTable> lootTables = this.rosePlugin.getManager(LootTableManager.class).getLootTables();
        if (lootTables.isEmpty())
            return Collections.singletonList("<no loaded loot tables>");

        return lootTables.stream()
                .map(LootTable::getName)
                .collect(Collectors.toList());
    }

}
