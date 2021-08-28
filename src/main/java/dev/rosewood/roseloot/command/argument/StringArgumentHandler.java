package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class StringArgumentHandler extends RoseCommandArgumentHandler<String> {

    public StringArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, String.class);
    }

    @Override
    protected String handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return argumentInstance.getArgument();
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Collections.singletonList(argumentInstance.getArgumentInfo().toString());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid String, cannot be empty";
    }

}
