package dev.rosewood.roseloot.command.framework;

import dev.rosewood.rosegarden.RosePlugin;

/**
 * Create one or more subclasses within a {@link RoseCommand} that extend this class.
 */
public abstract class RoseSubCommand extends RoseCommand {

    public RoseSubCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDescriptionKey() {
        return null;
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }

    @Override
    public final boolean hasHelp() {
        return false;
    }

}
