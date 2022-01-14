package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class FloatArgumentHandler extends RoseCommandArgumentHandler<Float> {

    public FloatArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Float.class);
    }

    @Override
    protected Float handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        try {
            return Float.parseFloat(input);
        } catch (Exception e) {
            throw new HandledArgumentException("Float [" + input + "] must be a number within bounds");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
