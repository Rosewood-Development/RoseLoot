package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class LongArgumentHandler extends RoseCommandArgumentHandler<Long> {

    public LongArgumentHandler() {
        super(Long.class);
    }

    @Override
    protected Long handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Long.parseLong(argumentInstance.getArgument());
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
        return "Invalid Long [" + argumentInstance.getArgument() + "], must be a whole number between -2^63 and 2^63-1 inclusively";
    }

}
