package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.List;

public class LootTableArgumentHandler extends ArgumentHandler<LootTable> {

    private final RosePlugin rosePlugin;

    public LootTableArgumentHandler(RosePlugin rosePlugin) {
        super(LootTable.class);

        this.rosePlugin = rosePlugin;
    }

    @Override
    public LootTable handle(CommandContext commandContext, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        LootTable value = this.rosePlugin.getManager(LootTableManager.class).getLootTable(input);
        if (value == null)
            throw new HandledArgumentException("argument-handler-loot-table", StringPlaceholders.of("input", input));
        return value;
    }

    @Override
    public List<String> suggest(CommandContext commandContext, Argument argument, String[] args) {
        List<LootTable> lootTables = this.rosePlugin.getManager(LootTableManager.class).getLootTables();
        if (lootTables.isEmpty())
            return List.of("<no loaded loot tables>");

        return lootTables.stream()
                .map(x -> x.getName().replace(' ', '_'))
                .toList();
    }

}
