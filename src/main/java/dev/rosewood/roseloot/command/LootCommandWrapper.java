package dev.rosewood.roseloot.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.utils.NMSUtil;
import java.util.List;

public class LootCommandWrapper extends RoseCommandWrapper {

    public LootCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "loot";
    }

    @Override
    public List<String> getDefaultAliases() {
        if (NMSUtil.isPaper()) {
            return List.of("roseloot", "rl");
        } else {
            return List.of("roseloot");
        }
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("dev.rosewood.roseloot.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
