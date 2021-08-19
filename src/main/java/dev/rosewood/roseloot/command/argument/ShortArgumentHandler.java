package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class ShortArgumentHandler extends RoseCommandArgumentHandler<Short> {

    public ShortArgumentHandler() {
        super(Short.class);
    }

    @Override
    protected Short handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Short.parseShort(argumentInstance.getArgument());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Collections.singletonList(argumentInstance.getArgumentInfo().toString());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid Short [" + argumentInstance.getArgument() + "], must be a whole number between -32,768 and 32,767 inclusively";
    }

}
