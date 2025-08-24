package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldArgumentHandler extends ArgumentHandler<World> {

    public WorldArgumentHandler() {
        super(World.class);
    }

    @Override
    public World handle(CommandContext commandContext, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        World world = Bukkit.getWorld(input);
        if (world == null)
            throw new HandledArgumentException("argument-handler-world", StringPlaceholders.of("input", input));
        return world;
    }

    @Override
    public List<String> suggest(CommandContext commandContext, Argument argument, String[] args) {
        return Bukkit.getWorlds().stream().map(World::getName).toList();
    }

}
