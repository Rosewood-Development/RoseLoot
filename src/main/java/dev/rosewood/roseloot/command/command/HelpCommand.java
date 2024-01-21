package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
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

    public HelpCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    @Override
    public void execute(CommandContext context) {
        super.execute(context);

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
