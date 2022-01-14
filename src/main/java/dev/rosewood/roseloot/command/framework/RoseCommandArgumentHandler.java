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
     * @param argumentInfo The argument info
     * @param argumentParser The argument parser
     * @return The String input converted to the handled object type, or null if the conversion failed
     */
    protected abstract T handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException;

    /**
     * The internal method for suggesting arguments
     *
     * @param argumentInfo The argument info
     * @param argumentParser The argument parser
     * @return A List of possible argument suggestions
     */
    protected abstract List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser);

    /**
     * Converts a String input from an argument instance into the handled type
     *
     * @param argumentInfo The argument info
     * @param argumentParser The argument parser
     * @return The String input converted to the handled object type, or null if the conversion failed
     */
    public final T handle(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        if (!argumentParser.hasNext())
            throw new HandledArgumentException("No more arguments are available, is there an error in the command syntax?");
        this.preProcess(argumentInfo);
        return this.handleInternal(argumentInfo, argumentParser);
    }

    /**
     * Gets command argument suggestions for the given argument instance
     *
     * @param argumentInfo The argument info
     * @param argumentParser The argument parser
     * @return A List of possible argument suggestions
     */
    public final List<String> suggest(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        this.preProcess(argumentInfo);
        return this.suggestInternal(argumentInfo, argumentParser);
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
     * @param argumentInfo The argument info to be handled
     */
    public void preProcess(RoseCommandArgumentInfo argumentInfo) {

    }

    /**
     * Thrown when an argument has an issue while parsing, the exception message is the reason why the argument failed to parse
     */
    public static class HandledArgumentException extends RuntimeException {

        public HandledArgumentException(String message) {
            super(message);
        }

    }

}
