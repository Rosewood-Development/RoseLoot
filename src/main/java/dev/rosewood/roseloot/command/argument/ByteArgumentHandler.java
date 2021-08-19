package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import java.util.Collections;
import java.util.List;

public class ByteArgumentHandler extends RoseCommandArgumentHandler<Byte> {

    public ByteArgumentHandler() {
        super(Byte.class);
    }

    @Override
    protected Byte handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        try {
            return Byte.parseByte(argumentInstance.getArgument());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Collections.singletonList(argumentInstance.getArgumentInfo().toString());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid Byte [" + argumentInstance.getArgument() + "], must be a whole number between -128 and 127 inclusively";
    }

}
