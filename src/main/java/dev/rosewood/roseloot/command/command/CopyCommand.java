package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
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
public class CopyCommand extends BaseRoseCommand {

    public CopyCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Boolean keepVanillaNBT) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        ItemStack itemStack = ((Player) context.getSender()).getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            localeManager.sendMessage(context.getSender(), "command-copy-no-item");
            return;
        }

        if (keepVanillaNBT == null)
            keepVanillaNBT = false;

        String entry = ItemLootItem.toSection(itemStack, keepVanillaNBT);
        ComponentBuilder builder = new ComponentBuilder()
                .append(TextComponent.fromLegacyText(localeManager.getLocaleMessage("prefix")))
                .append(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-copy-success")))
                .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(localeManager.getLocaleMessage("command-copy-hover")))));
        context.getSender().spigot().sendMessage(builder.create());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("copy")
                .descriptionKey("command-copy-description")
                .permission("roseloot.copy")
                .playerOnly(true)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .optional("keepVanillaNBT", ArgumentHandlers.BOOLEAN)
                .build();
    }

}
