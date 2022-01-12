package dev.rosewood.roseloot.command.framework;

import dev.rosewood.rosegarden.RosePlugin;
import java.util.List;

public abstract class RoseCommandArgumentHandler<T> {

    protected RosePlugin rosePlugin;
    protected Class<T> handledType;

    public RoseCommandArgumentHandler(RosePlugin rosePlugin, Class<T> handledType) {
        this.rosePlugin = rosePlugin;
        this.handledType = handledType;
    }

    /**
     * The internal method for converting a String input into the handled type
     *
     * @param context The command context
     * @param argumentInstance The argument instance
     * @return The String input converted to the handled object type, or null if the conversion failed
     */
    protected abstract T handleInternal(CommandContext context, ArgumentInstance argumentInstance);

    /**
     * The internal method for suggesting arguments
     *
     * @param context The command context
     * @param argumentInstance The argument instance
     * @return A List of possible argument suggestions
     */
    protected abstract List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance);

    /**
     * Gets the error message that will be displayed to the user upon a failed handling of the given String input
     *
     * @param context The command context
     * @param argumentInstance The argument instance
     * @return The String message to be displayed to the user
     */
    public abstract String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance);

    /**
     * Converts a String input from an argument instance into the handled type
     *
     * @param context The command context
     * @param argumentInstance The argument instance
     * @return The String input converted to the handled object type, or null if the conversion failed
     */
    public final T handle(CommandContext context, ArgumentInstance argumentInstance) {
        this.preProcess(argumentInstance);
        return this.handleInternal(context, argumentInstance);
    }

    /**
     * Gets command argument suggestions for the given argument instance
     *
     * @param context The command context
     * @param argumentInstance The argument instance
     * @return A List of possible argument suggestions
     */
    public final List<String> suggest(CommandContext context, ArgumentInstance argumentInstance) {
        this.preProcess(argumentInstance);
        return this.suggestInternal(context, argumentInstance);
    }

    /**
     * Checks if a String argument can be parsed by this argument handler
     *
     * @param context The command context
     * @param argumentInstance The argument instance
     * @return true if the input is valid for this argument handler, false otherwise
     */
    public boolean isInvalid(CommandContext context, ArgumentInstance argumentInstance) {
        this.preProcess(argumentInstance);
        String input = argumentInstance.getArgument();
        if (input == null || input.trim().isEmpty())
            return !argumentInstance.getArgumentInfo().isOptional();
        return this.handleInternal(context, argumentInstance) == null;
    }

    /**
     * @return the Class that this argument handler handles
     */
    public Class<T> getHandledType() {
        return this.handledType;
    }

    /**
     * Allows an argument handler to preprocess the argument before handling or suggesting
     *
     * @param argumentInstance The argument instance about to be handled
     */
    public void preProcess(ArgumentInstance argumentInstance) {

    }

}
