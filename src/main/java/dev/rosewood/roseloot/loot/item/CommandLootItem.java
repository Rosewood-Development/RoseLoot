package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Optional;
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
    public void trigger(LootContext context, Location location) {
        World world = location.getWorld();
        if (world == null)
            return;

        Optional<Player> player = context.getLootingPlayer();
        if (!this.command.contains("%player%") || player.isPresent())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), context.getPlaceholders().apply(this.command));
    }

    public static CommandLootItem fromSection(ConfigurationSection section) {
        if (!section.contains("value"))
            return null;
        return new CommandLootItem(section.getString("value"));
    }

}
