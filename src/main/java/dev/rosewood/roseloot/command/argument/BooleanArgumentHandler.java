package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Arrays;
import java.util.List;

public class BooleanArgumentHandler extends RoseCommandArgumentHandler<Boolean> {

    public BooleanArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Boolean.class);
    }

    @Override
    protected Boolean handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Boolean.parseBoolean(argumentInstance.getArgument());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Arrays.asList("true", "false");
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid Boolean [" + argumentInstance.getArgument() + "], must be true or false";
    }

}
