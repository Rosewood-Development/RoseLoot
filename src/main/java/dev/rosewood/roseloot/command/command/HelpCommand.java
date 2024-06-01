package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.manager.LocaleManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

@SuppressWarnings("deprecation")
public class HelpCommand extends dev.rosewood.rosegarden.command.HelpCommand {

    public HelpCommand(RosePlugin rosePlugin, BaseRoseCommand parent) {
        super(rosePlugin, parent, CommandInfo.builder("help").descriptionKey("command-description-help").build());
    }

    @Override
    protected void sendCustomHelpMessage(CommandContext context) {
        if (context.getSender().hasPermission("roseloot.help.wiki")) {
            LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);
            String url = localeManager.getLocaleMessage("command-help-wiki-url");
            TextComponent link = new TextComponent(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-help-wiki")));
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-help-wiki-hover", StringPlaceholders.of("url", url))))));
            context.getSender().spigot().sendMessage(link);
        }
    }

}
