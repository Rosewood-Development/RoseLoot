package dev.rosewood.roseloot.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.util.VanillaLootTableConverter;
import java.io.File;

public class ConvertCommand extends BaseRoseCommand {

    public ConvertCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        File directory = new File(context.getRosePlugin().getDataFolder(), "convert");
        File output = new File(context.getRosePlugin().getDataFolder(), "convert_output");

        directory.mkdirs();
        output.mkdirs();

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            localeManager.sendCommandMessage(context.getSender(), "command-convert-no-files");
            return;
        }

        VanillaLootTableConverter.convertDirectory(directory, output);
        localeManager.sendCommandMessage(context.getSender(), "command-convert-finished");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("convert")
                .descriptionKey("command-convert-description")
                .permission("roseloot.convert")
                .build();
    }

}
