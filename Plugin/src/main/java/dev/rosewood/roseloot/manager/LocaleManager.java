package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import org.bukkit.command.CommandSender;

public class LocaleManager extends AbstractLocaleManager {

    public LocaleManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void sendPrefixedText(CommandSender sender, String text) {
        String prefix = this.getLocaleMessage("prefix");
        this.sendUnparsedMessage(sender, prefix + text);
    }

}
