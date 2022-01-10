package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
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
    protected LootTable handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return this.rosePlugin.getManager(LootTableManager.class).getLootTable(argumentInstance.getArgument());
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        List<LootTable> lootTables = this.rosePlugin.getManager(LootTableManager.class).getLootTables();
        if (lootTables.isEmpty())
            return Collections.singletonList("<no loaded loot tables>");

        return lootTables.stream()
                .map(LootTable::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid LootTable [" + argumentInstance.getArgument() + "]";
    }

}
