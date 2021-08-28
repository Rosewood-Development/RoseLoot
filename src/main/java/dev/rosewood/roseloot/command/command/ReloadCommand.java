package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommand;
import dev.rosewood.roseloot.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.Collections;
import java.util.List;

public class ReloadCommand extends RoseCommand {

    public ReloadCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        this.rosePlugin.reload();
        this.rosePlugin.getManager(LocaleManager.class).sendMessage(context.getSender(), "command-reload-reloaded");
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescriptionKey() {
        return "command-reload-description";
    }

    @Override
    public String getRequiredPermission() {
        return "roseloot.reload";
    }

}
