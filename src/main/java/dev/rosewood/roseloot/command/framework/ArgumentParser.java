package dev.rosewood.roseloot.command.framework;

import java.util.List;

public class ArgumentParser {

    private final CommandContext context;
    private final List<String> arguments;
    private String previous;

    public ArgumentParser(CommandContext context, List<String> arguments) {
        this.context = context;
        this.arguments = arguments;
        this.previous = "";
    }

    /**
     * @return the command context
     */
    public CommandContext getContext() {
        return this.context;
    }

    /**
     * @return true if there is another argument available, false otherwise
     */
    public boolean hasNext() {
        return !this.arguments.isEmpty();
    }

    /**
     * @return pops the next available argument, or an empty string if none are available
     */
    public String next() {
        if (!this.hasNext())
            return "";
        this.previous = this.arguments.remove(0);
        return this.previous;
    }

    /**
     * @return the previously returned argument from calling {@link #next()}
     */
    public String previous() {
        return this.previous;
    }

    /**
     * @return peeks the next available argument, or an empty string if none are available
     */
    public String peek() {
        if (!this.hasNext())
            return "";
        return this.arguments.get(0);
    }

}
