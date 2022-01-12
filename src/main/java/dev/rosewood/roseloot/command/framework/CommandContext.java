package dev.rosewood.roseloot.command.framework;

import org.bukkit.command.CommandSender;

public class CommandContext {

    private final CommandSender sender;
    private final String[] args;

    public CommandContext(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    /**
     * @return the executor of the command
     */
    public CommandSender getSender() {
        return this.sender;
    }

    /**
     * @return the raw unparsed arguments of the command
     */
    public String[] getArgs() {
        return this.args;
    }

}
