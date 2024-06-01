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
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenerateCommand extends BaseRoseCommand {

    public GenerateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, LootTable lootTable, Player player, Boolean silent) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        CommandSender sender = context.getSender();
        if (!(sender instanceof Player) && player == null) {
            localeManager.sendMessage(sender, "command-generate-requires-player");
            return;
        }

        if (lootTable.getType() != LootTableTypes.LOOT_TABLE) {
            localeManager.sendMessage(sender, "command-generate-invalid-loot-table-type");
            return;
        }

        Player target = player == null ? (Player) sender : player;
        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(target))
                .put(LootContextParams.ORIGIN, target.getLocation())
                .put(LootContextParams.LOOTER, target)
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(lootTable, lootContext);
        lootResult.getLootContents().dropForPlayer(target);

        if (silent == null || !silent)
            localeManager.sendMessage(sender, "command-generate-success", StringPlaceholders.of("player", target.getName(), "loottable", lootTable.getName()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("generate")
                .descriptionKey("command-generate-description")
                .permission("roseloot.generate")
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
