package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CopyCommand extends RoseCommand {

    public CopyCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        ItemStack itemStack = ((Player) context.getSender()).getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            localeManager.sendMessage(context.getSender(), "command-copy-no-item");
            return;
        }

        String entry = ItemLootItem.toSection(itemStack);
        ComponentBuilder builder = new ComponentBuilder()
                .append(TextComponent.fromLegacyText(localeManager.getLocaleMessage("prefix")))
                .append(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-copy-success")))
                .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-copy-hover")))));
        context.getSender().spigot().sendMessage(builder.create());
    }

    @Override
    protected String getDefaultName() {
        return "copy";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescriptionKey() {
        return "command-copy-description";
    }

    @Override
    public String getRequiredPermission() {
        return "roseloot.copy";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
