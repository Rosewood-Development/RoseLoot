package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.manager.CooldownManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.Collection;
import java.util.List;
import org.bukkit.entity.Player;

public class CooldownsCommand extends RoseCommand {

    public CooldownsCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, RoseSubCommand command) {

    }

    public static class CooldownsListCommand extends RoseSubCommand {

        public CooldownsListCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Optional Player player) {
            CooldownManager cooldownManager = this.rosePlugin.getManager(CooldownManager.class);
            LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

            Collection<CooldownManager.Cooldown> cooldowns;
            if (player == null) {
                cooldowns = cooldownManager.getActiveCooldowns(null);
            } else {
                cooldowns = cooldownManager.getActiveCooldowns(player.getUniqueId());
            }

            if (cooldowns.isEmpty()) {
                localeManager.sendMessage(context.getSender(), "command-cooldowns-list-none", StringPlaceholders.of(
                        "target", getDisplay(player)
                ));
                return;
            }

            localeManager.sendMessage(context.getSender(), "command-cooldowns-list-header", StringPlaceholders.of(
                    "target", getDisplay(player),
                    "amount", cooldowns.size()
            ));

            for (CooldownManager.Cooldown cooldown : cooldowns) {
                localeManager.sendSimpleMessage(context.getSender(), "command-cooldowns-list-entry", StringPlaceholders.of(
                        "cooldown", cooldown.id(),
                        "time", cooldown.toString()
                ));
            }
        }

        @Override
        protected String getDefaultName() {
            return "list";
        }

    }

    public static class CooldownsResetCommand extends RoseSubCommand {

        public CooldownsResetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Optional Player player) {
            CooldownManager cooldownManager = this.rosePlugin.getManager(CooldownManager.class);
            LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

            cooldownManager.resetCooldowns(player == null ? null : player.getUniqueId());
            localeManager.sendMessage(context.getSender(), "command-cooldowns-reset", StringPlaceholders.of(
                    "target", getDisplay(player)
            ));
        }

        @Override
        protected String getDefaultName() {
            return "reset";
        }

    }

    private static String getDisplay(Player player) {
        return player == null ? "Global" : player.getName();
    }

    @Override
    protected String getDefaultName() {
        return "cooldowns";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return List.of();
    }

    @Override
    public String getDescriptionKey() {
        return "command-cooldowns-description";
    }

    @Override
    public String getRequiredPermission() {
        return "roseloot.cooldowns";
    }

}
