package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.manager.LocaleManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

@SuppressWarnings("deprecation")
public class HelpCommand extends dev.rosewood.rosegarden.command.command.HelpCommand {

    private static final String WIKI_URL = "https://github.com/Rosewood-Development/RoseLoot/wiki/Loot-Tables-Overview";

    public HelpCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        localeManager.sendMessage(context.getSender(), "command-help-title");
        for (RoseCommand command : this.parent.getCommands()) {
            if (!command.hasHelp() || !command.canUse(context.getSender()))
                continue;

            StringPlaceholders stringPlaceholders = StringPlaceholders.of(
                    "cmd", this.parent.getName(),
                    "subcmd", command.getName().toLowerCase(),
                    "args", command.getArgumentsString(),
                    "desc", localeManager.getLocaleMessage(command.getDescriptionKey())
            );

            localeManager.sendSimpleMessage(context.getSender(), "command-help-list-description" + (command.getNumParameters() == 0 ? "-no-args" : ""), stringPlaceholders);
        }

        if (context.getSender().hasPermission("roseloot.help.wiki")) {
            TextComponent link = new TextComponent(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-help-wiki")));
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WIKI_URL));
            link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-help-wiki-hover", StringPlaceholders.of("url", WIKI_URL))))));
            context.getSender().spigot().sendMessage(link);
        }
    }

}
