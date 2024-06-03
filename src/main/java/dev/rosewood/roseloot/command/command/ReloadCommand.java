package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class ReloadCommand extends dev.rosewood.rosegarden.command.ReloadCommand {

    public ReloadCommand(RosePlugin rosePlugin) {
        super(rosePlugin, CommandInfo.builder("reload").descriptionKey("command-reload-description").permission("roseloot.reload").build());
    }

}
