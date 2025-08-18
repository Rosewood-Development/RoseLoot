package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.manager.LocaleManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class CopyComponentsCommand extends BaseRoseCommand {

    public CopyComponentsCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        ItemStack itemStack = ((Player) context.getSender()).getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            localeManager.sendMessage(context.getSender(), "command-copy-components-no-item");
            return;
        }

        String entry = ItemLootItem.toComponentsSection(itemStack);
        ComponentBuilder builder = new ComponentBuilder()
                .append(TextComponent.fromLegacyText(localeManager.getLocaleMessage("prefix")))
                .append(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-copy-components-success")))
                .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-copy-components-hover")))));
        context.getSender().spigot().sendMessage(builder.create());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("copycomponents")
                .descriptionKey("command-copy-components-description")
                .permission("roseloot.copycomponents")
                .playerOnly(true)
                .build();
    }

}
