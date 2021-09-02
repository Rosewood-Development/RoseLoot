package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommand;
import dev.rosewood.roseloot.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Collections;
import java.util.List;

public class ListCommand extends RoseCommand {

    public ListCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        LootTableManager lootTableManager = this.rosePlugin.getManager(LootTableManager.class);

        List<LootTable> lootTables = lootTableManager.getLootTables();
        if (!lootTables.isEmpty()) {
            localeManager.sendMessage(context.getSender(), "command-list-header", StringPlaceholders.single("amount", lootTables.size()));
            for (LootTable lootTable : lootTables)
                localeManager.sendSimpleMessage(context.getSender(), "command-list-entry", StringPlaceholders.builder("name", lootTable.getName()).addPlaceholder("type", lootTable.getType().name()).build());
        } else {
            localeManager.sendMessage(context.getSender(), "command-list-none");
        }
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescriptionKey() {
        return "command-list-description";
    }

    @Override
    public String getRequiredPermission() {
        return "roseloot.list";
    }

}
