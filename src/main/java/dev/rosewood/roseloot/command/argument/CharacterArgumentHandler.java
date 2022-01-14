package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.Collections;
import java.util.List;

public class CharacterArgumentHandler extends RoseCommandArgumentHandler<Character> {

    public CharacterArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Character.class);
    }

    @Override
    protected Character handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        if (input.length() != 1)
            throw new HandledArgumentException("Character [" + input + "] must be exactly 1 character");
        return input.charAt(0);
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Collections.singletonList(argumentInfo.toString());
    }

}
