package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.PrimaryCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.utils.NMSUtil;
import java.util.ArrayList;
import java.util.List;

public class BaseCommand extends PrimaryCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        List<RoseCommand> subcommands = new ArrayList<>(List.of(
                new CooldownsCommand(this.rosePlugin),
                new ConvertCommand(this.rosePlugin),
                new CopyCommand(this.rosePlugin),
                new GenerateCommand(this.rosePlugin),
                new GiveItemsCommand(this.rosePlugin),
                new HelpCommand(this.rosePlugin, this),
                new ListCommand(this.rosePlugin),
                new LoggingReloadCommand(this.rosePlugin),
                new SpawnCommand(this.rosePlugin)
        ));

        if (NMSUtil.getVersionNumber() > 21 || (NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3))
            subcommands.add(3, new CopyComponentsCommand(this.rosePlugin));

        return CommandInfo.builder("loot")
                .permission("roseloot.basecommand")
                .aliases(NMSUtil.isPaper() ? List.of("roseloot", "rl") : List.of("roseloot"))
                .arguments(ArgumentsDefinition.builder()
                        .optionalSub(subcommands.toArray(RoseCommand[]::new)))
                .build();
    }

}
