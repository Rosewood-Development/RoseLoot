package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class LongArgumentHandler extends RoseCommandArgumentHandler<Long> {

    public LongArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Long.class);
    }

    @Override
    protected Long handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            throw new HandledArgumentException("Long [" + input + "] must be a whole number between -2^63 and 2^63-1 inclusively");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
