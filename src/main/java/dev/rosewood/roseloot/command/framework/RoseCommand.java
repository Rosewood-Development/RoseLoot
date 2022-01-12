package dev.rosewood.roseloot.command.framework;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.AbstractLocaleManager;
import dev.rosewood.roseloot.command.framework.annotation.Inject;
import dev.rosewood.roseloot.command.framework.annotation.Optional;
import dev.rosewood.roseloot.command.framework.annotation.RoseExecutable;
import dev.rosewood.roseloot.manager.CommandManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.permissions.Permissible;

/**
 * Ensure a {@code public void} method annotated with {@link RoseExecutable} with a first parameter of {@link CommandContext} exists.
 * All following parameters after the first must have a matching {@link RoseCommandArgumentHandler} to be valid.
 */
public abstract class RoseCommand implements Comparable<RoseCommand> {

    protected final RosePlugin rosePlugin;
    private final Map<String, RoseSubCommand> subCommands;

    public RoseCommand(RosePlugin rosePlugin, Class<?>... subCommandClasses) {
        this.rosePlugin = rosePlugin;
        this.subCommands = new HashMap<>();
        this.generateSubCommands(subCommandClasses);
        this.validateExecuteMethod();
    }

    /**
     * @return the name of the command
     */
    public abstract String getName();

    /**
     * @return any aliases that can be used as an alternative to the main command name
     */
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * @return the description key for this command's description to be displayed in the help menu, requires an {@link AbstractLocaleManager} implementation
     */
    public abstract String getDescriptionKey();

    /**
     * @return the required permission to be able to run this command
     */
    public abstract String getRequiredPermission();

    /**
     * @return the method annotated with {@link RoseExecutable}
     */
    public Method getExecuteMethod() {
        return Stream.of(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(RoseExecutable.class))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * @return a list of all arguments part of the {@link RoseExecutable}
     */
    public List<RoseCommandArgumentInfo> getArgumentInfo() {
        List<RoseCommandArgumentInfo> argumentInfo = new ArrayList<>();
        Parameter[] parameters = this.getParameters();
        for (int i = 0; i < parameters.length; i++)
            argumentInfo.add(new RoseCommandArgumentInfo(parameters[i], i));
        return argumentInfo;
    }

    /**
     * @return a Map of all {@link RoseSubCommand} registered for this command
     */
    public Map<String, RoseSubCommand> getSubCommands() {
        return Collections.unmodifiableMap(this.subCommands);
    }

    /**
     * @return the index of the {@link RoseSubCommand} argument in the command syntax or -1 if there is no subcommand
     */
    public int getSubCommandArgumentIndex() {
        return this.getArgumentInfo().stream()
                .filter(RoseCommandArgumentInfo::isSubCommand)
                .map(RoseCommandArgumentInfo::getIndex)
                .findFirst()
                .orElse(-1);
    }

    /**
     * @return true if there is a {@link RoseSubCommand} within this command's arguments, false otherwise
     */
    public boolean hasSubCommand() {
        return this.getSubCommandArgumentIndex() != -1;
    }

    /**
     * @return a displayable output of this command's parameters
     */
    public String getArgumentsString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<RoseCommandArgumentInfo> iterator = this.getArgumentInfo().iterator();
        while (iterator.hasNext()) {
            RoseCommandArgumentInfo argument = iterator.next();
            stringBuilder.append(argument);

            if (argument.isSubCommand())
                break;

            if (iterator.hasNext())
                stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    /**
     * @return the number of total arguments for this command
     */
    public int getNumParameters() {
        return this.getParameters().length - (!this.getExecuteMethod().getParameters()[0].isAnnotationPresent(Inject.class) ? 0 : 1);
    }

    /**
     * @return the number of optional arguments for this command
     */
    public int getNumOptionalParameters() {
        return Math.toIntExact(this.getArgumentInfo().stream().filter(RoseCommandArgumentInfo::isOptional).count());
    }

    /**
     * @return the number of required arguments for this command
     */
    public int getNumRequiredArguments() {
        return this.getNumParameters() - this.getNumOptionalParameters();
    }

    /**
     * @return an array of Parameters for this command's {@link RoseExecutable}
     */
    private Parameter[] getParameters() {
        return Stream.of(this.getExecuteMethod().getParameters())
                .filter(x -> x.getType() != CommandContext.class)
                .filter(x -> !x.isAnnotationPresent(Inject.class))
                .toArray(Parameter[]::new);
    }

    /**
     * Checks if this command can be run by a Permissible
     *
     * @param permissible The Permissible to check
     * @return true if the Permissible can execute this command, false otherwise
     */
    public boolean canUse(Permissible permissible) {
        return this.getRequiredPermission() == null || permissible.hasPermission(this.getRequiredPermission());
    }

    /**
     * @return true if this command will be displayed in the help menu, false otherwise
     */
    public boolean hasHelp() {
        return true;
    }

    /**
     * @return true if this command can only be run by a Player, false otherwise
     */
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public int compareTo(RoseCommand other) {
        return this.getName().compareTo(other.getName());
    }

    /**
     * Validates that the command has a valid {@link RoseExecutable} layout.
     * The following conditions must be met:
     * <ul>
     *     <li>Must have a {@code public void} method annotated with {@link RoseExecutable}</li>
     *     <li>First parameter must be a {@link CommandContext}</li>
     *     <li>All parameters must have a registered {@link RoseCommandArgumentHandler} (excluding {@link CommandContext} and {@link RoseSubCommand})</li>
     *     <li>Primitive typed parameters must not be marked as {@link Optional}, use wrapped types instead</li>
     *     <li>If a parameter is marked as {@link Optional} then all subsequent parameters must also be marked as {@link Optional}</li>
     *     <li>If the last parameter is a {@link RoseSubCommand}, there must be at least one registered {@link RoseSubCommand}</li>
     *     <li>No parameters are allowed after the {@link RoseSubCommand}</li>
     * </ul>
     *
     * @throws InvalidRoseCommandArgumentsException if any of the above conditions are not met
     */
    private void validateExecuteMethod() {
        try {
            this.getExecuteMethod();
        } catch (IllegalStateException e) {
            throw new InvalidRoseCommandArgumentsException("No method marked as RoseExecutable detected");
        }

        Parameter[] rawParameters = this.getExecuteMethod().getParameters();
        if (rawParameters.length == 0 || rawParameters[0].getType() != CommandContext.class)
            throw new InvalidRoseCommandArgumentsException("First method parameter is not a CommandContext");

        CommandManager commandManager = this.rosePlugin.getManager(CommandManager.class);
        boolean first = true;
        boolean optionalFound = false;
        boolean subCommandFound = false;
        for (Parameter parameter : rawParameters) {
            if (first) {
                first = false;
                continue;
            } else if (parameter.getType() == CommandContext.class) {
                throw new InvalidRoseCommandArgumentsException("Only the first parameter may be a CommandContext");
            }

            if (subCommandFound)
                throw new InvalidRoseCommandArgumentsException("Parameters after a RoseSubCommand are not allowed");

            if (optionalFound && !parameter.isAnnotationPresent(Optional.class))
                throw new InvalidRoseCommandArgumentsException("Parameter '" + parameter.getType().getSimpleName() + " " + parameter.getName() + "' must be marked as Optional because a previous parameter was already marked as Optional");

            try {
                commandManager.resolveArgumentHandler(parameter.getType());
            } catch (IllegalStateException e) {
                throw new InvalidRoseCommandArgumentsException("Parameter '" + parameter.getType().getSimpleName() + " " + parameter.getName() + "' is missing a RoseCommandArgumentHandler");
            }

            if (parameter.isAnnotationPresent(Optional.class)) {
                if (parameter.getType().isPrimitive())
                    throw new InvalidRoseCommandArgumentsException("Parameter '" + parameter.getType().getSimpleName() + " " + parameter.getName() + "' is primitive but is marked as Optional. Change to a " + LootUtils.getPrimitiveAsWrapper(parameter.getType()) + " instead");

                optionalFound = true;
            }

            if (parameter.getType() == RoseSubCommand.class)
                subCommandFound = true;
        }

        if (subCommandFound && this.subCommands.isEmpty())
            throw new InvalidRoseCommandArgumentsException("No subcommands are registered but at least one is required");
    }

    /**
     * Locates and registers {@link RoseSubCommand} classes as subcommands
     *
     * @param subCommandClasses An array of provided classes
     */
    private void generateSubCommands(Class<?>[] subCommandClasses) {
        Set<Class<?>> subClasses = new HashSet<>(Arrays.asList(subCommandClasses));
        subClasses.addAll(Arrays.asList(this.getClass().getDeclaredClasses()));

        for (Class<?> clazz : subClasses) {
            if (!RoseSubCommand.class.isAssignableFrom(clazz))
                continue;

            @SuppressWarnings("unchecked")
            Class<RoseSubCommand> subCommandClass = (Class<RoseSubCommand>) clazz;
            RoseSubCommand subCommandInstance;
            try {
                Constructor<RoseSubCommand> pluginConstructor = subCommandClass.getConstructor(RosePlugin.class);
                subCommandInstance = pluginConstructor.newInstance(this.rosePlugin);
            } catch (ReflectiveOperationException e) {
                try {
                    Constructor<RoseSubCommand> pluginConstructor = subCommandClass.getConstructor();
                    subCommandInstance = pluginConstructor.newInstance();
                } catch (ReflectiveOperationException e2) {
                    throw new IllegalStateException("Invalid RoseSubCommand constructor for [" + subCommandClass.getName() + "]. Requires an empty constructor or one that accepts a RosePlugin.");
                }
            }

            this.subCommands.put(subCommandInstance.getName().toLowerCase(), subCommandInstance);
            List<String> aliases = subCommandInstance.getAliases();
            if (aliases != null)
                for (String alias : aliases)
                    this.subCommands.put(alias.toLowerCase(), subCommandInstance);
        }
    }

    public static class InvalidRoseCommandArgumentsException extends RuntimeException {

        public InvalidRoseCommandArgumentsException(String message) {
            super(message);
        }

    }

}
