package dev.rosewood.roseloot.command.framework;

import dev.rosewood.roseloot.command.framework.annotation.Optional;
import java.lang.reflect.Parameter;

public class RoseCommandArgumentInfo {

    private final Parameter parameter;

    public RoseCommandArgumentInfo(Parameter parameter) {
        this.parameter = parameter;
    }

    public Class<?> getType() {
        return this.parameter.getType();
    }

    public String getName() {
        return this.parameter.getName();
    }

    public boolean isOptional() {
        return this.parameter.isAnnotationPresent(Optional.class);
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
