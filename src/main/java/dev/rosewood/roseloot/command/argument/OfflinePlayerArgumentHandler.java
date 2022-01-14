package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
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
    protected OfflinePlayer handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        return Bukkit.getOfflinePlayer(argumentParser.next());
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
