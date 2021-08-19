package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class IntegerArgumentHandler extends RoseCommandArgumentHandler<Integer> {

    public IntegerArgumentHandler() {
        super(Integer.class);
    }

    @Override
    protected Integer handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Integer.parseInt(argumentInstance.getArgument());
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
        return "Invalid Integer [" + argumentInstance.getArgument() + "], must be a whole number between -2^31 and 2^31-1 inclusively";
    }

}
