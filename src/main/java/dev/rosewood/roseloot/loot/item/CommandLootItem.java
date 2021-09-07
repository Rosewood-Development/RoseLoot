package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandLootItem implements TriggerableLootItem<String> {

    private final String command;

    public CommandLootItem(String command) {
        this.command = command;
    }

    @Override
    public String create(LootContext context) {
        return this.command;
    }

    @Override
    public void trigger(LootContext context, Player player, Location location) {
        World world = location.getWorld();
        if (world == null)
            return;

        StringPlaceholders.Builder stringPlaceholdersBuilder = StringPlaceholders.builder("world", world.getName())
                .addPlaceholder("x", location.getX())
                .addPlaceholder("y", location.getY())
                .addPlaceholder("z", location.getZ());

        boolean isPlayer = player != null;
        if (isPlayer)
            stringPlaceholdersBuilder.addPlaceholder("player", player.getName());

        StringPlaceholders stringPlaceholders = stringPlaceholdersBuilder.build();
        if (!this.command.contains("%player%") || isPlayer)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stringPlaceholders.apply(this.command));
    }

    public static CommandLootItem fromSection(ConfigurationSection section) {
        if (!section.contains("value"))
            return null;
        return new CommandLootItem(section.getString("value"));
    }

}
