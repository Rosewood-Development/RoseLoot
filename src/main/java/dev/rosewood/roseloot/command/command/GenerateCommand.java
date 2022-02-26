package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        Player target = player == null ? (Player) sender : player;
        LootContext lootContext = new LootContext(target);
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(lootTable, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        // Drop items and experience
        target.getInventory().addItem(lootContents.getItems().toArray(new ItemStack[0])).forEach((x, y) -> target.getWorld().dropItem(target.getLocation(), y));

        int experience = lootContents.getExperience();
        if (experience > 0) {
            Location location = target.getLocation();
            target.getWorld().spawn(location, ExperienceOrb.class, x -> x.setExperience(experience));
        }

        lootContents.triggerExtras(target.getLocation());

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
