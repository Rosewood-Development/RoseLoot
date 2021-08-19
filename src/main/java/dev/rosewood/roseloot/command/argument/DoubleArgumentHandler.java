package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class DoubleArgumentHandler extends RoseCommandArgumentHandler<Double> {

    public DoubleArgumentHandler() {
        super(Double.class);
    }

    @Override
    protected Double handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Double.parseDouble(argumentInstance.getArgument());
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
        return "Invalid Double [" + argumentInstance.getArgument() + "], must be a number within bounds";
    }

}
