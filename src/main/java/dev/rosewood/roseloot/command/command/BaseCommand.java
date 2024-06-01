package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.PrimaryCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.utils.NMSUtil;
import java.util.List;

public class BaseCommand extends PrimaryCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("loot")
                .permission("roseloot.basecommand")
                .aliases(NMSUtil.isPaper() ? List.of("roseloot", "rl") : List.of("roseloot"))
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .optionalSub(
                        new CooldownsCommand(this.rosePlugin),
                        new CopyCommand(this.rosePlugin),
                        new GenerateCommand(this.rosePlugin),
                        new GiveItemsCommand(this.rosePlugin),
                        new HelpCommand(this.rosePlugin, this),
                        new ListCommand(this.rosePlugin),
                        new ReloadCommand(this.rosePlugin)
                );
    }

}
