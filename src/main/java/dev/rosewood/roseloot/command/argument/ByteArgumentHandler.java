package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class ByteArgumentHandler extends RoseCommandArgumentHandler<Byte> {

    public ByteArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Byte.class);
    }

    @Override
    protected Byte handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        try {
            return Byte.parseByte(input);
        } catch (Exception e) {
            throw new HandledArgumentException("Byte [" + input + "] must be a whole number between -128 and 127 inclusively");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
