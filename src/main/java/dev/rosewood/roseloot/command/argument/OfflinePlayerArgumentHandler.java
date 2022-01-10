package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class OfflinePlayerArgumentHandler extends RoseCommandArgumentHandler<OfflinePlayer> {

    public OfflinePlayerArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, OfflinePlayer.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected OfflinePlayer handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Bukkit.getOfflinePlayer(argumentInstance.getArgument());
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "No Player with the username [" + argumentInstance.getArgument() + "] was found";
    }

}
