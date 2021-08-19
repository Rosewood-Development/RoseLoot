package dev.rosewood.roseloot.command.framework;

import java.util.List;

public abstract class RoseCommandArgumentHandler<T> {

    protected Class<T> handledType;

    public RoseCommandArgumentHandler(Class<T> handledType) {
        this.handledType = handledType;
    }

    protected abstract T handleInternal(CommandContext context, ArgumentInstance argumentInstance);

    protected abstract List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance);

    public abstract String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance);

    public final T handle(CommandContext context, ArgumentInstance argumentInstance) {
        this.preProcess(argumentInstance);
        return this.handleInternal(context, argumentInstance);
    }

    public final List<String> suggest(CommandContext context, ArgumentInstance argumentInstance) {
        this.preProcess(argumentInstance);
        return this.suggestInternal(context, argumentInstance);
    }

    public boolean isInvalid(CommandContext context, String input, ArgumentInstance argumentInstance) {
        this.preProcess(argumentInstance);
        if (input == null || input.trim().isEmpty())
            return !argumentInstance.getArgumentInfo().isOptional();
        return this.handleInternal(context, argumentInstance) == null;
    }

    public Class<T> getHandledType() {
        return this.handledType;
    }

    public void preProcess(ArgumentInstance argumentInstance) {

    }

}
