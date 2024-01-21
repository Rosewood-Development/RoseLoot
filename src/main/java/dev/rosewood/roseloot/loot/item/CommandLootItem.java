package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.StringProvider;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandLootItem implements TriggerableLootItem {

    private final StringProvider command;

    public CommandLootItem(StringProvider command) {
        this.command = command;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Player> player = context.getLootingPlayer();
        String command = this.command.get(context);
        if (!command.contains("%player%") || player.isPresent())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), context.applyPlaceholders(command));
    }

    public static CommandLootItem fromSection(ConfigurationSection section) {
        StringProvider value = StringProvider.fromSection(section, "value", null);
        if (value == null)
            return null;
        return new CommandLootItem(value);
    }

}
