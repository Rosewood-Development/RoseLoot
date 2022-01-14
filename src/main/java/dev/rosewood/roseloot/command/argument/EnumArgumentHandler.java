package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgumentHandler<T extends Enum<T>> extends RoseCommandArgumentHandler<T> {

    public EnumArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, null); // This is a special case and will be handled by the preprocessor
    }

    @Override
    protected T handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        String input = argumentParser.next();
        T[] enumConstants = this.getHandledType().getEnumConstants();
        Optional<T> value = Stream.of(enumConstants)
                .filter(x -> x.name().equalsIgnoreCase(input))
                .findFirst();

        if (!value.isPresent()) {
            String message;
            if (enumConstants.length <= 10) {
                message = this.handledType.getSimpleName() + " type [" + input + "]. Valid types: " +
                        Stream.of(enumConstants).map(x -> x.name().toLowerCase()).collect(Collectors.joining(", "));
            } else {
                message = this.handledType.getSimpleName() + " type [" + input + "]";
            }
            throw new HandledArgumentException(message);
        }

        return value.get();
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Stream.of(this.getHandledType().getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void preProcess(RoseCommandArgumentInfo argumentInfo) {
        this.handledType = (Class<T>) argumentInfo.getType();
    }

}
