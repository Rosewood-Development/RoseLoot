package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class FloatArgumentHandler extends RoseCommandArgumentHandler<Float> {

    public FloatArgumentHandler() {
        super(Float.class);
    }

    @Override
    protected Float handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Float.parseFloat(argumentInstance.getArgument());
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
        return "Invalid Float [" + argumentInstance.getArgument() + "], must be a number within bounds";
    }

}
