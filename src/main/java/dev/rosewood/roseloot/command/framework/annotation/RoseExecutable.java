package dev.rosewood.roseloot.command.framework.annotation;

import dev.rosewood.roseloot.command.framework.RoseCommand;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method in a {@link RoseCommand} as an executable command
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoseExecutable {
}
