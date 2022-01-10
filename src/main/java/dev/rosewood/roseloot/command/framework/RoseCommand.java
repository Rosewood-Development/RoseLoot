package dev.rosewood.roseloot.command.framework;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.annotation.Inject;
import dev.rosewood.roseloot.command.framework.annotation.RoseExecutable;
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
 * All following parameters after the first must have a matching RoseCommandArgumentHandler to be valid.
 */
public abstract class RoseCommand implements Comparable<RoseCommand> {

    protected final RosePlugin rosePlugin;
    private final Map<String, RoseSubCommand> subCommands;

    public RoseCommand(RosePlugin rosePlugin, Class<?>... subCommandClasses) {
        this.rosePlugin = rosePlugin;
        this.subCommands = new HashMap<>();
        this.generateSubCommands(subCommandClasses);
    }

    public abstract String getName();

    public List<String> getAliases() {
        return Collections.emptyList();
    }

    public abstract String getDescriptionKey();

    public abstract String getRequiredPermission();

    public Method getExecuteMethod() {
        return Stream.of(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(RoseExecutable.class))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public List<RoseCommandArgumentInfo> getArgumentInfo() {
        List<RoseCommandArgumentInfo> argumentInfo = new ArrayList<>();
        Parameter[] parameters = this.getParameters();
        for (int i = 0; i < parameters.length; i++)
            argumentInfo.add(new RoseCommandArgumentInfo(parameters[i], i));
        return argumentInfo;
    }

    public Map<String, RoseSubCommand> getSubCommands() {
        return Collections.unmodifiableMap(this.subCommands);
    }

    public int getSubCommandArgumentIndex() {
        return this.getArgumentInfo().stream()
                .filter(RoseCommandArgumentInfo::isSubCommand)
                .map(RoseCommandArgumentInfo::getIndex)
                .findFirst()
                .orElse(-1);
    }

    public boolean hasSubCommand() {
        return this.getSubCommandArgumentIndex() != -1;
    }

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

    public int getNumParameters() {
        return this.getParameters().length - (!this.getExecuteMethod().getParameters()[0].isAnnotationPresent(Inject.class) ? 0 : 1);
    }

    public int getNumOptionalParameters() {
        return Math.toIntExact(this.getArgumentInfo().stream().filter(RoseCommandArgumentInfo::isOptional).count());
    }

    public int getNumRequiredArguments() {
        return this.getNumParameters() - this.getNumOptionalParameters();
    }

    private Parameter[] getParameters() {
        return Stream.of(this.getExecuteMethod().getParameters())
                .filter(x -> x.getType() != CommandContext.class)
                .filter(x -> !x.isAnnotationPresent(Inject.class))
                .toArray(Parameter[]::new);
    }

    public boolean canUse(Permissible permissible) {
        return this.getRequiredPermission() == null || permissible.hasPermission(this.getRequiredPermission());
    }

    public boolean hasHelp() {
        return true;
    }

    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public int compareTo(RoseCommand other) {
        return this.getName().compareTo(other.getName());
    }

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

}
