package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommand;
import dev.rosewood.roseloot.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.manager.CommandManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends RoseCommand {

    public HelpCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        CommandManager commandManager = this.rosePlugin.getManager(CommandManager.class);
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        localeManager.sendMessage(context.getSender(), "command-help-title");
        for (RoseCommand command : commandManager.getCommands()) {
            if (!command.hasHelp() || !command.canUse(context.getSender()))
                continue;

            StringPlaceholders stringPlaceholders = StringPlaceholders.builder("cmd", command.getName().toLowerCase())
                    .addPlaceholder("args", command.getArgumentsString())
                    .addPlaceholder("desc", localeManager.getLocaleMessage(command.getDescriptionKey()))
                    .build();
            localeManager.sendSimpleMessage(context.getSender(), "command-description", stringPlaceholders);
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescriptionKey() {
        return "command-help-description";
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }

}
