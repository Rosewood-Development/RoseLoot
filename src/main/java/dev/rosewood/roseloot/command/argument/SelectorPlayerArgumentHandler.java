package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.roseloot.command.types.SelectorPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SelectorPlayerArgumentHandler extends RoseCommandArgumentHandler<SelectorPlayer> {

    public SelectorPlayerArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, SelectorPlayer.class);
    }

    @Override
    protected SelectorPlayer handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        if (input.startsWith("@")) {
            // Running a selector, try to find exactly one entity which must be a player
            List<Entity> entities;
            try {
                entities = Bukkit.selectEntities(argumentParser.getContext().getSender(), input);
            } catch (Exception e) {
                throw new HandledArgumentException("Selector has a syntax error");
            }

            if (entities.isEmpty())
                throw new HandledArgumentException("No Players were found for the given selector");

            if (entities.size() > 1)
                throw new HandledArgumentException("Selector resulted in multiple entities being selected");

            Entity selected = entities.get(0);
            if (!(selected instanceof Player))
                throw new HandledArgumentException("Selector resulted in a selected non-player entity");

            return new SelectorPlayer((Player) selected);
        }

        Player player = Bukkit.getPlayer(input);
        if (player == null)
            throw new HandledArgumentException("No Player with the username [" + input + "] was found online");
        return new SelectorPlayer(player);
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        List<String> suggestions = new ArrayList<>(Arrays.asList("@p", "@r"));
        suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        return suggestions;
    }

}
