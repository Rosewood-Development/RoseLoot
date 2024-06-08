package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.manager.CooldownManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.rosestacker.lib.rosegarden.command.framework.annotation.Optional;
import java.util.Collection;
import org.bukkit.entity.Player;

public class CooldownsCommand extends BaseRoseCommand {

    public CooldownsCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("cooldowns")
                .descriptionKey("command-cooldowns-description")
                .permission("roseloot.cooldowns")
                .arguments(ArgumentsDefinition.builder()
                        .requiredSub(
                                new CooldownsListCommand(this.rosePlugin),
                                new CooldownsResetCommand(this.rosePlugin)
                        ))
                .build();
    }

    public static class CooldownsListCommand extends BaseRoseCommand {

        public CooldownsListCommand(RosePlugin rosePlugin) {
            super(rosePlugin);
        }

        @RoseExecutable
        public void execute(CommandContext context, Player player) {
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
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("list")
                    .arguments(ArgumentsDefinition.builder()
                            .optional("player", ArgumentHandlers.PLAYER)
                            .build())
                    .build();
        }

    }

    public static class CooldownsResetCommand extends BaseRoseCommand {

        public CooldownsResetCommand(RosePlugin rosePlugin) {
            super(rosePlugin);
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
        protected CommandInfo createCommandInfo() {
            return CommandInfo.builder("reset")
                    .arguments(ArgumentsDefinition.builder()
                            .optional("player", ArgumentHandlers.PLAYER)
                            .build())
                    .build();
        }

    }

    private static String getDisplay(Player player) {
        return player == null ? "Global" : player.getName();
    }

}
