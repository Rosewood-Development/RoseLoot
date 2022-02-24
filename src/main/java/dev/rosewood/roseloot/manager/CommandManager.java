package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import dev.rosewood.roseloot.command.LootCommandWrapper;
import java.util.Collections;
import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return Collections.singletonList(LootCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return Collections.singletonList("dev.rosewood.roseloot.command.argument");
    }

}
