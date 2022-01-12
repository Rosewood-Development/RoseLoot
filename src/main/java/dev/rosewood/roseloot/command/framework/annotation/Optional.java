package dev.rosewood.roseloot.command.framework.annotation;

import dev.rosewood.roseloot.command.framework.RoseCommand;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a {@link RoseExecutable} method parameter in a {@link RoseCommand} as optional
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
