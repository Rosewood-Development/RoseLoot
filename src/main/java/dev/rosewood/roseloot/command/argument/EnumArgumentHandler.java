package dev.rosewood.roseloot.command.argument;

import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.CommandContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.util.StringUtil;

public class EnumArgumentHandler<T extends Enum<T>> extends RoseCommandArgumentHandler<T> {

    public EnumArgumentHandler() {
        super(null); // This is a special case and will be handled by the preprocessor
    }

    @Override
    protected T handleInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Stream.of(this.getHandledType().getEnumConstants())
                .filter(x -> x.name().equalsIgnoreCase(argumentInstance.getArgument()))
                .findFirst()
                .orElse(null);
    }

    @Override
    protected List<String> suggestInternal(CommandContext context, ArgumentInstance argumentInstance) {
        return Stream.of(this.getHandledType().getEnumConstants())
                .map(Enum::name)
                .filter(x -> StringUtil.startsWithIgnoreCase(x, argumentInstance.getArgument()))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @Override
    public String getErrorMessage(CommandContext context, ArgumentInstance argumentInstance) {
        return "Invalid " + this.handledType.getSimpleName() + " type [" + argumentInstance.getArgument() + "]. Valid types: " +
                Stream.of(this.getHandledType().getEnumConstants()).map(x -> x.name().toLowerCase()).collect(Collectors.joining(", "));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void preProcess(ArgumentInstance argumentInstance) {
        this.handledType = (Class<T>) argumentInstance.getArgumentInfo().getType();
    }

}
