package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenerateCommand extends RoseCommand {

    public GenerateCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, LootTable lootTable, @Optional Player player, @Optional Boolean silent) {
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
            localeManager.sendMessage(sender, "command-generate-success", StringPlaceholders.builder("player", target.getName()).addPlaceholder("loottable", lootTable.getName()).build());
    }

    @Override
    protected String getDefaultName() {
        return "generate";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getDescriptionKey() {
        return "command-generate-description";
    }

    @Override
    public String getRequiredPermission() {
        return "roseloot.generate";
    }

}
