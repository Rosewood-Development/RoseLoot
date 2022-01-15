package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;

public class TestCommand extends RoseCommand {

    public TestCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, LootTable lootTable, @Optional Player player) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
        localeManager.sendCustomMessage(context.getSender(), "&eThis command is still under construction, please try again later :)");
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescriptionKey() {
        return "command-test-description";
    }

    @Override
    public String getRequiredPermission() {
        return "rosegarden.test";
    }

}
