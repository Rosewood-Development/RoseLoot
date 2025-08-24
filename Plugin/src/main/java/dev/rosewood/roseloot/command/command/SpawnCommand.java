package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.command.argument.LootArgumentHandlers;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseRoseCommand {

    public SpawnCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, LootTable lootTable, World world, double x, double y, double z, Player player) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        if (lootTable.getType() != LootTableTypes.LOOT_TABLE) {
            localeManager.sendMessage(context.getSender(), "command-spawn-invalid-loot-table-type");
            return;
        }

        Location location = new Location(world, x, y, z);

        LootContext.Builder contextBuilder;
        if (player != null) {
            contextBuilder = LootContext.builder(LootUtils.getEntityLuck(player))
                    .put(LootContextParams.LOOTER, player);
        } else {
            contextBuilder = LootContext.builder();
        }
        contextBuilder.put(LootContextParams.ORIGIN, location);

        LootContext lootContext = contextBuilder.build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(lootTable, lootContext);
        lootResult.getLootContents().dropAtLocation(location);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("spawn")
                .descriptionKey("command-spawn-description")
                .permission("roseloot.spawn")
                .arguments(ArgumentsDefinition.builder()
                        .required("loottable", LootArgumentHandlers.LOOT_TABLE)
                        .required("world", LootArgumentHandlers.WORLD)
                        .required("x", ArgumentHandlers.DOUBLE)
                        .required("y", ArgumentHandlers.DOUBLE)
                        .required("z", ArgumentHandlers.DOUBLE)
                        .optional("player", ArgumentHandlers.PLAYER)
                        .build())
                .build();
    }

}
