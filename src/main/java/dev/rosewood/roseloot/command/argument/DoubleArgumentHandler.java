package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class DoubleArgumentHandler extends RoseCommandArgumentHandler<Double> {

    public DoubleArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Double.class);
    }

    @Override
    protected Double handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            throw new HandledArgumentException("Double [" + input + "] must be a number within bounds");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
