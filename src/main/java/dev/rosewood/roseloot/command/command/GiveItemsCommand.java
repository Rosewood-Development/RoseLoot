package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.command.argument.LootArgumentHandlers;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemsCommand extends BaseRoseCommand {

    public GiveItemsCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, LootTable lootTable, Player player, Boolean silent) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        CommandSender sender = context.getSender();
        if (!(sender instanceof Player) && player == null) {
            localeManager.sendMessage(sender, "command-giveitems-requires-player");
            return;
        }

        Player target = player == null ? (Player) sender : player;

        LootContext lootContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, target.getLocation())
                .put(LootContextParams.LOOTER, target)
                .build();

        List<ItemStack> items = lootTable.getAllItems(lootContext);
        if (items.isEmpty()) {
            localeManager.sendMessage(sender, "command-giveitems-empty");
            return;
        }

        target.getInventory().addItem(items.toArray(new ItemStack[0])).forEach((x, y) -> target.getWorld().dropItemNaturally(target.getLocation(), y));

        if (silent == null || !silent)
            localeManager.sendMessage(sender, "command-giveitems-success", StringPlaceholders.of("player", target.getName(), "loottable", lootTable.getName()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("giveitems")
                .descriptionKey("command-giveitems-description")
                .permission("roseloot.giveitems")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("loottable", LootArgumentHandlers.LOOT_TABLE)
                .optional("player", ArgumentHandlers.PLAYER)
                .optional("silent", ArgumentHandlers.BOOLEAN)
                .build();
    }

}
