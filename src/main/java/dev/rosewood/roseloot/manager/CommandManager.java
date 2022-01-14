package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.ClassUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.command.argument.EnumArgumentHandler;
import dev.rosewood.roseloot.command.framework.ArgumentParser;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommand;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.roseloot.command.framework.RoseSubCommand;
import dev.rosewood.roseloot.util.LootUtils;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

@SuppressWarnings("rawtypes")
public class CommandManager extends Manager implements TabExecutor {

    private static final String COMMAND_PACKAGE = "dev.rosewood.roseloot.command.command";
    private static final String ARGUMENT_PACKAGE = "dev.rosewood.roseloot.command.argument";

    private final Map<Class<? extends RoseCommandArgumentHandler>, RoseCommandArgumentHandler<?>> argumentHandlers;
    private final Map<String, RoseCommand> commandLookupMap;
    private final LocaleManager localeManager;

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.argumentHandlers = new HashMap<>();
        this.commandLookupMap = new HashMap<>();
        this.localeManager = this.rosePlugin.getManager(LocaleManager.class);
    }

    @Override
    public void reload() {
        try {
            // Load arguments
            for (Class<RoseCommandArgumentHandler> argumentHandlerClass : ClassUtils.getClassesOf(this.rosePlugin, ARGUMENT_PACKAGE, RoseCommandArgumentHandler.class)) {
                // Ignore abstract/interface classes
                if (Modifier.isAbstract(argumentHandlerClass.getModifiers()) || Modifier.isInterface(argumentHandlerClass.getModifiers()))
                    continue;

                RoseCommandArgumentHandler<?> argumentHandler = argumentHandlerClass.getConstructor(RosePlugin.class).newInstance(this.rosePlugin);
                this.argumentHandlers.put(argumentHandlerClass, argumentHandler);
            }

            // Load commands
            for (Class<RoseCommand> commandClass : ClassUtils.getClassesOf(this.rosePlugin, COMMAND_PACKAGE, RoseCommand.class)) {
                // Ignore abstract/interface classes
                if (Modifier.isAbstract(commandClass.getModifiers()) || Modifier.isInterface(commandClass.getModifiers()))
                    continue;

                // Subcommands get loaded within commands
                if (RoseSubCommand.class.isAssignableFrom(commandClass))
                    continue;

                RoseCommand command = commandClass.getConstructor(RosePlugin.class).newInstance(this.rosePlugin);
                this.commandLookupMap.put(command.getName().toLowerCase(), command);
                List<String> aliases = command.getAliases();
                if (aliases != null)
                    aliases.forEach(x -> this.commandLookupMap.put(x.toLowerCase(), command));
            }
        } catch (Exception e) {
            this.rosePlugin.getLogger().severe("Fatal error initializing commands");
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
        this.argumentHandlers.clear();
        this.commandLookupMap.clear();
    }

    public RoseCommandArgumentHandler<?> resolveArgumentHandler(Class<?> handledParameterClass) {
        if (Enum.class.isAssignableFrom(handledParameterClass))
            return this.argumentHandlers.get(EnumArgumentHandler.class);

        // Map primitive types to their wrapper handlers
        if (handledParameterClass.isPrimitive())
            handledParameterClass = LootUtils.getPrimitiveAsWrapper(handledParameterClass);

        Class<?> finalHandledParameterClass = handledParameterClass;
        return this.argumentHandlers.values()
                .stream()
                .filter(x -> x.getHandledType() != null && x.getHandledType() == finalHandledParameterClass)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tried to resolve a RoseCommandArgumentHandler for an unhandled type"));
    }

    public RoseCommand getCommand(String commandName) {
        return this.commandLookupMap.get(commandName);
    }

    public List<RoseCommand> getCommands() {
        return this.commandLookupMap.values().stream()
                .distinct()
                .sorted(Comparator.comparing(RoseCommand::getName))
                .collect(Collectors.toList());
    }

    public RoseSubCommand getSubCommand(RoseCommand command, String commandName) {
        return command.getSubCommands().get(commandName.toLowerCase());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (args.length == 0) {
                String baseColor = this.localeManager.getLocaleMessage("base-command-color");
                this.localeManager.sendCustomMessage(sender, baseColor + "Running <g:#8A2387:#E94057:#F27121>" + this.rosePlugin.getDescription().getName() + baseColor + " v" + this.rosePlugin.getDescription().getVersion());
                this.localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41E0F0:#FF8DCE>" + this.rosePlugin.getDescription().getAuthors().get(0));
                this.localeManager.sendSimpleMessage(sender, "base-command-help");
                return true;
            }

            RoseCommand command = this.getCommand(args[0]);
            if (command == null) {
                this.localeManager.sendMessage(sender, "unknown-command");
                return true;
            }

            String[] cmdArgs = new String[args.length - 1];
            System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);
            CommandContext context = new CommandContext(sender, cmdArgs);
            ArgumentParser argumentParser = new ArgumentParser(context, new LinkedList<>(Arrays.asList(cmdArgs)));

            this.runCommand(sender, command, argumentParser, new ArrayList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            this.localeManager.sendCustomMessage(sender, "&cAn unknown error occurred; details have been printed to console. Please contact a server administrator.");
        }
        return true;
    }

    private void runCommand(CommandSender sender, RoseCommand command, ArgumentParser argumentParser, List<Object> parsedArgs, int commandLayer) throws ReflectiveOperationException {
        if (!command.canUse(sender)) {
            this.localeManager.sendMessage(sender, "no-permission");
            return;
        }

        if (command.isPlayerOnly() && !(sender instanceof Player)) {
            this.localeManager.sendMessage(sender, "only-player");
            return;
        }

        // Start parsing parameters based on the command requirements, print errors out as we go
        for (RoseCommandArgumentInfo argumentInfo : command.getArgumentInfo()) {
            if (!argumentParser.hasNext()) {
                // All other arguments are optional, this is fine
                if (argumentInfo.isOptional())
                    break;

                // Ran out of arguments while parsing
                if (command.hasSubCommand()) {
                    this.localeManager.sendMessage(sender, "missing-arguments-extra", StringPlaceholders.single("amount", command.getNumRequiredArguments()));
                } else {
                    this.localeManager.sendMessage(sender, "missing-arguments", StringPlaceholders.single("amount", parsedArgs.size() + command.getNumRequiredArguments() + commandLayer));
                }
                return;
            }

            if (argumentInfo.isSubCommand()) {
                RoseSubCommand subCommand = this.getSubCommand(command, argumentParser.next());
                if (subCommand == null) {
                    this.localeManager.sendMessage(sender, "invalid-subcommand");
                    return;
                }

                this.runCommand(sender, subCommand, argumentParser, parsedArgs, commandLayer + 1);
                return;
            }

            try {
                Object parsedArgument = this.resolveArgumentHandler(argumentInfo.getType()).handle(argumentInfo, argumentParser);
                if (parsedArgument == null) {
                    this.localeManager.sendMessage(sender, "invalid-argument-null", StringPlaceholders.single("name", argumentInfo.toString()));
                    return;
                }

                parsedArgs.add(parsedArgument);
            } catch (RoseCommandArgumentHandler.HandledArgumentException e) {
                this.localeManager.sendMessage(sender, "invalid-argument", StringPlaceholders.single("message", e.getMessage()));
                return;
            }
        }

        this.executeCommand(argumentParser.getContext(), command, parsedArgs);
    }

    private void executeCommand(CommandContext context, RoseCommand command, List<Object> parsedArgs) throws ReflectiveOperationException {
        Stream.Builder<Object> argumentBuilder = Stream.builder().add(context);
        parsedArgs.forEach(argumentBuilder::add);

        // Fill optional parameters with nulls
        for (int i = parsedArgs.size(); i < command.getNumParameters(); i++)
            argumentBuilder.add(null);

        command.getExecuteMethod().invoke(command, argumentBuilder.build().toArray());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0)
            return new ArrayList<>(this.commandLookupMap.keySet());

        if (args.length <= 1)
            return this.commandLookupMap.keySet().stream()
                    .filter(x -> StringUtil.startsWithIgnoreCase(x, args[args.length - 1]))
                    .collect(Collectors.toList());

        RoseCommand command = this.getCommand(args[0]);
        if (command == null)
            return Collections.emptyList();

        String[] cmdArgs = new String[args.length - 1];
        System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);
        CommandContext context = new CommandContext(sender, cmdArgs);
        ArgumentParser argumentParser = new ArgumentParser(context, new LinkedList<>(Arrays.asList(cmdArgs)));

        return this.tabCompleteCommand(sender, command, argumentParser);
    }

    private List<String> tabCompleteCommand(CommandSender sender, RoseCommand command, ArgumentParser argumentParser) {
        if (!command.canUse(sender) || (command.isPlayerOnly() && !(sender instanceof Player)))
            return Collections.emptyList();

        // Consume all arguments until there are no more, then print those results
        for (RoseCommandArgumentInfo argumentInfo : command.getArgumentInfo()) {
            if (argumentInfo.isSubCommand()) {
                if (!argumentParser.hasNext())
                    return new ArrayList<>(command.getSubCommands().keySet());

                String input = argumentParser.next();
                RoseSubCommand subCommand = this.getSubCommand(command, input);
                if (subCommand == null)
                    return command.getSubCommands().keySet()
                            .stream()
                            .filter(x -> StringUtil.startsWithIgnoreCase(x, input))
                            .collect(Collectors.toList());

                if (argumentParser.hasNext())
                    return this.tabCompleteCommand(sender, subCommand, argumentParser);

                return Collections.emptyList();
            }

            List<String> suggestions = this.resolveArgumentHandler(argumentInfo.getType()).suggest(argumentInfo, argumentParser);
            String input = argumentParser.previous();
            if (!argumentParser.hasNext())
                return suggestions.stream()
                        .filter(x -> StringUtil.startsWithIgnoreCase(x, input))
                        .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
