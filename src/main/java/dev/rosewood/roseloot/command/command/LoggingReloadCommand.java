package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoggingReloadCommand extends ReloadCommand {

    private static CommandSender reloadSender = null;

    public LoggingReloadCommand(RosePlugin rosePlugin) {
        super(rosePlugin, CommandInfo.builder("reload").descriptionKey("command-reload-description").permission("roseloot.reload").build());
    }

    @Override
    @RoseExecutable
    public void execute(CommandContext context) {
        if (context.getSender() instanceof Player sender) {
            reloadSender = sender;
            super.execute(context);
            Bukkit.getScheduler().runTaskLater(this.rosePlugin, () -> {
                reloadSender = null;
            }, 10L);
        } else {
            super.execute(context);
        }
    }

    public static CommandSender getReloadSender() {
        return reloadSender;
    }

}
