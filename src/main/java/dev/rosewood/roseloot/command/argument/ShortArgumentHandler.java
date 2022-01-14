package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class ShortArgumentHandler extends RoseCommandArgumentHandler<Short> {

    public ShortArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Short.class);
    }

    @Override
    protected Short handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        try {
            return Short.parseShort(input);
        } catch (Exception e) {
            throw new HandledArgumentException("Short [" + input + "] must be a whole number between -32,768 and 32,767 inclusively");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
