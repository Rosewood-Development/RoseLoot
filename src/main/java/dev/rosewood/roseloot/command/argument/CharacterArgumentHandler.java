package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class CharacterArgumentHandler extends RoseCommandArgumentHandler<Character> {

    public CharacterArgumentHandler() {
        super(Character.class);
    }

    @Override
    protected Character handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        if (argumentInstance.getArgument().length() != 1)
            return null;
        return argumentInstance.getArgument().charAt(0);
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Collections.singletonList(argumentInstance.getArgumentInfo().toString());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid Character [" + argumentInstance.getArgument() + "], must be exactly 1 character";
    }

}
