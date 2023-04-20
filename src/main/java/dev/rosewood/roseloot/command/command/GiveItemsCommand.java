package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.manager.LocaleManager;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemsCommand extends RoseCommand {

    public GiveItemsCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, LootTable lootTable, @Optional Player player, @Optional Boolean silent) {
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
    protected String getDefaultName() {
        return "giveitems";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return List.of();
    }

    @Override
    public String getDescriptionKey() {
        return "command-giveitems-description";
    }

    @Override
    public String getRequiredPermission() {
        return "roseloot.giveitems";
    }

}
