package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Arrays;
import java.util.List;

public class BooleanArgumentHandler extends RoseCommandArgumentHandler<Boolean> {

    public BooleanArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Boolean.class);
    }

    @Override
    protected Boolean handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        try {
            return Boolean.parseBoolean(input);
        } catch (Exception e) {
            throw new HandledArgumentException("Boolean [" + input + "] must be true or false");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Arrays.asList("true", "false");
    }

}
