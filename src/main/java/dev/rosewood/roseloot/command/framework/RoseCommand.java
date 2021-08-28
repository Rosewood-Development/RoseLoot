package dev.rosewood.roseloot.command.framework;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.command.framework.annotation.RoseExecutable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.permissions.Permissible;

/**
 * Ensure a {@code public void} method annotated with {@link RoseExecutable} with a first parameter of {@link CommandContext} exists.
 * All following parameters after the first must have a matching RoseCommandArgumentHandler to be valid.
 */
public abstract class RoseCommand implements Comparable<RoseCommand> {

    protected final RosePlugin rosePlugin;

    public RoseCommand(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    public abstract String getName();

    public abstract List<String> getAliases();

    public abstract String getDescriptionKey();

    public abstract String getRequiredPermission();

    public Method getExecuteMethod() {
        return Stream.of(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(RoseExecutable.class))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public List<RoseCommandArgumentInfo> getArgumentInfo() {
        return Stream.of(this.getParameters())
                .map(RoseCommandArgumentInfo::new)
                .collect(Collectors.toList());
    }

    public String getArgumentsString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<RoseCommandArgumentInfo> iterator = this.getArgumentInfo().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toString());
            if (iterator.hasNext())
                stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    public int getNumParameters() {
        return this.getParameters().length - 1;
    }

    public int getNumOptionalParameters() {
        return Math.toIntExact(this.getArgumentInfo().stream().filter(RoseCommandArgumentInfo::isOptional).count());
    }

    public int getNumRequiredArguments() {
        return this.getNumParameters() - this.getNumOptionalParameters();
    }

    private Parameter[] getParameters() {
        return Stream.of(this.getExecuteMethod().getParameters())
                .skip(1)
                .toArray(Parameter[]::new);
    }

    public boolean canUse(Permissible permissible) {
        return this.getRequiredPermission() == null || permissible.hasPermission(this.getRequiredPermission());
    }

    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public int compareTo(RoseCommand other) {
        return this.getName().compareTo(other.getName());
    }

}
