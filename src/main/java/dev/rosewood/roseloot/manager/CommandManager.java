package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import java.util.Collections;
import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<String> getCommandPackages() {
        return Collections.singletonList("dev.rosewood.roseloot.command.command");
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return Collections.singletonList("dev.rosewood.roseloot.command.argument");
    }

    @Override
    public String getCommandName() {
        return "rl";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

}
