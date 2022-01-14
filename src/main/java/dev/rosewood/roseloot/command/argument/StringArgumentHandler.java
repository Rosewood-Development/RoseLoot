package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class StringArgumentHandler extends RoseCommandArgumentHandler<String> {

    public StringArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, String.class);
    }

    @Override
    protected String handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        if (input.trim().isEmpty())
            throw new HandledArgumentException("String cannot be empty");
        return input;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
