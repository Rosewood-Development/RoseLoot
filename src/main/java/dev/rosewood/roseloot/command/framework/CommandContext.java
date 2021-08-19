package dev.rosewood.roseloot.command.framework;

import org.bukkit.command.CommandSender;

public class CommandContext {

    private final CommandSender sender;
    private final String[] args;

    public CommandContext(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public String[] getArgs() {
        return this.args;
    }

}
