package dev.rosewood.roseloot.command.framework;

import dev.rosewood.roseloot.command.framework.annotation.Optional;
import java.lang.reflect.Parameter;

public class RoseCommandArgumentInfo {

    private final Parameter parameter;
    private final int index;

    public RoseCommandArgumentInfo(Parameter parameter, int index) {
        this.parameter = parameter;
        this.index = index;
    }

    public Class<?> getType() {
        return this.parameter.getType();
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.parameter.getName();
    }

    public boolean isOptional() {
        return this.parameter.isAnnotationPresent(Optional.class);
    }

    public boolean isSubCommand() {
        return this.getType() == RoseSubCommand.class;
    }

    @Override
    public String toString() {
        if (this.isOptional()) {
            return "[" + this.getName() + "]";
        } else {
            return "<" + this.getName() + ">";
        }
    }

}
