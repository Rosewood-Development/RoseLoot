package dev.rosewood.roseloot.command.framework;

/**
 * Holds information about a parsed argument
 */
public class ArgumentInstance {

    private final RoseCommandArgumentInfo argumentInfo;
    private final RoseCommandArgumentHandler<?> argumentHandler;
    private final String argument;

    public ArgumentInstance(RoseCommandArgumentInfo argumentInfo, RoseCommandArgumentHandler<?> argumentHandler, String argument) {
        this.argumentInfo = argumentInfo;
        this.argumentHandler = argumentHandler;
        this.argument = argument;
    }

    public RoseCommandArgumentInfo getArgumentInfo() {
        return this.argumentInfo;
    }

    public RoseCommandArgumentHandler<?> getArgumentHandler() {
        return this.argumentHandler;
    }

    public String getArgument() {
        return this.argument;
    }

}
