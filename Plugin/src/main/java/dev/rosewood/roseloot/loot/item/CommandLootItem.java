package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.StringProvider;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandLootItem implements TriggerableLootItem {

    private final StringProvider command;
    private final boolean runAsPlayer;

    protected CommandLootItem(StringProvider command, boolean runAsPlayer) {
        this.command = command;
        this.runAsPlayer = runAsPlayer;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Player> player = context.getLootingPlayer();
        String command = this.command.get(context);
        if (this.runAsPlayer) {
            player.ifPresent(x -> this.dispatchCommand(x, context.applyPlaceholders(command)));
        } else {
            if (!command.contains("%player%") || player.isPresent())
                this.dispatchCommand(Bukkit.getConsoleSender(), context.applyPlaceholders(command));
        }
    }

    private void dispatchCommand(CommandSender sender, String command) {
        if (NMSUtil.isFolia()) {
            RoseLoot.getInstance().getScheduler().runTask(() -> Bukkit.dispatchCommand(sender, command));
        } else {
            Bukkit.dispatchCommand(sender, command);
        }
    }

    public static CommandLootItem fromSection(ConfigurationSection section) {
        StringProvider value = StringProvider.fromSection(section, "value", null);
        if (value == null)
            return null;
        boolean runAsPlayer = section.getBoolean("run-as-player", false);
        return new CommandLootItem(value, runAsPlayer);
    }

}
