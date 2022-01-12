package dev.rosewood.roseloot.command.framework.annotation;

import dev.rosewood.roseloot.command.framework.RoseSubCommand;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a {@link RoseExecutable} method parameter in a {@link RoseSubCommand} as inherited from the parent command
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
