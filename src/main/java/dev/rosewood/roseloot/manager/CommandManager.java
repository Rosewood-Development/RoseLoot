package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.ClassUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.command.argument.EnumArgumentHandler;
import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommand;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.roseloot.command.framework.RoseSubCommand;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
            // Load commands
            for (Class<RoseCommand> commandClass : ClassUtils.getClassesOf(this.rosePlugin, COMMAND_PACKAGE, RoseCommand.class)) {
                // Subcommands get loaded within commands
                if (RoseSubCommand.class.isAssignableFrom(commandClass))
                    continue;

                RoseCommand command = commandClass.getConstructor(RosePlugin.class).newInstance(this.rosePlugin);
                this.commandLookupMap.put(command.getName().toLowerCase(), command);
                List<String> aliases = command.getAliases();
                if (aliases != null)
                    aliases.forEach(x -> this.commandLookupMap.put(x.toLowerCase(), command));
            }

            // Load arguments
            for (Class<RoseCommandArgumentHandler> argumentHandlerClass : ClassUtils.getClassesOf(this.rosePlugin, ARGUMENT_PACKAGE, RoseCommandArgumentHandler.class)) {
                RoseCommandArgumentHandler<?> argumentHandler = argumentHandlerClass.getConstructor(RosePlugin.class).newInstance(this.rosePlugin);
                this.argumentHandlers.put(argumentHandlerClass, argumentHandler);
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
                .orElseThrow(IllegalStateException::new);
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
                this.localeManager.sendCustomMessage(sender, baseColor + "Running <g:#8A2387:#E94057:#F27121>RoseLoot" + baseColor + " v" + this.rosePlugin.getDescription().getVersion());
                this.localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41e0f0:#ff8dce>" + this.rosePlugin.getDescription().getAuthors().get(0));
                this.localeManager.sendSimpleMessage(sender, "base-command-help");
                return true;
            }

            RoseCommand command = this.getCommand(args[0]);
            if (command == null) {
                this.localeManager.sendMessage(sender, "unknown-command");
                return true;
            }

            this.runCommand(sender, command, args, new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            this.localeManager.sendCustomMessage(sender, "&cAn unknown error occurred; details have been printed to console. Please contact a server administrator.");
        }
        return true;
    }

    private void runCommand(CommandSender sender, RoseCommand command, String[] args, List<ArgumentInstance> parsedArgs) throws ReflectiveOperationException {
        if (!command.canUse(sender)) {
            this.localeManager.sendMessage(sender, "no-permission");
            return;
        }

        if (command.isPlayerOnly() && !(sender instanceof Player)) {
            this.localeManager.sendMessage(sender, "only-player");
            return;
        }

        if (command.getNumRequiredArguments() > args.length - 1) {
            if (command.hasSubCommand()) {
                this.localeManager.sendMessage(sender, "missing-arguments-extra", StringPlaceholders.single("amount", command.getNumRequiredArguments()));
            } else {
                this.localeManager.sendMessage(sender, "missing-arguments", StringPlaceholders.single("amount", parsedArgs.size() + command.getNumRequiredArguments()));
            }
            return;
        }

        String[] cmdArgs = new String[args.length - 1];
        System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
        CommandContext context = new CommandContext(sender, cmdArgs);

        List<RoseCommandArgumentInfo> argumentInfo = command.getArgumentInfo();
        for (int i = 0; i < argumentInfo.size() && i < cmdArgs.length; i++) {
            RoseCommandArgumentInfo argInfo = argumentInfo.get(i);
            parsedArgs.add(new ArgumentInstance(argInfo, !argInfo.isSubCommand() ? this.resolveArgumentHandler(argInfo.getType()) : null, cmdArgs[i]));
        }

        List<ArgumentInstance> invalidArgs = parsedArgs.stream()
                .filter(x -> !x.getArgumentInfo().isSubCommand() && x.getArgumentHandler().isInvalid(context, x.getArgument(), x))
                .collect(Collectors.toList());

        if (!invalidArgs.isEmpty()) {
            if (invalidArgs.size() == 1) {
                this.localeManager.sendMessage(sender, "invalid-argument-header");
            } else {
                this.localeManager.sendMessage(sender, "invalid-arguments-header");
            }

            for (ArgumentInstance invalidArgument : invalidArgs)
                this.localeManager.sendSimpleMessage(sender, "invalid-argument", StringPlaceholders.single("message", invalidArgument.getArgumentHandler().getErrorMessage(context, invalidArgument)));
            return;
        }

        int argumentPosition = args.length - 1;
        if (argumentPosition > command.getNumRequiredArguments() - (command.hasSubCommand() ? 1 : 0)) {
            if (!command.hasSubCommand()) {
                this.executeCommand(context, command, parsedArgs);
                return;
            }

            int subCommandIndex = command.getSubCommandArgumentIndex();
            if (argumentPosition <= subCommandIndex) {
                this.executeCommand(context, command, parsedArgs);
                return;
            }

            RoseSubCommand subCommand = this.getSubCommand(command, args[subCommandIndex + 1]);
            if (subCommand == null) {
                this.localeManager.sendMessage(sender, "invalid-subcommand");
                return;
            }

            String[] subCmdArgs = new String[args.length - subCommandIndex - 1];
            System.arraycopy(args, subCommandIndex + 1, subCmdArgs, 0, subCmdArgs.length);

            this.runCommand(sender, subCommand, subCmdArgs, parsedArgs);
            return;
        }

        this.executeCommand(context, command, parsedArgs);
    }

    private void executeCommand(CommandContext context, RoseCommand command, List<ArgumentInstance> parsedArgs) throws ReflectiveOperationException {
        Stream.Builder<Object> argumentBuilder = Stream.builder().add(context);
        for (ArgumentInstance argumentInstance : parsedArgs)
            if (!argumentInstance.getArgumentInfo().isSubCommand())
                argumentBuilder.add(argumentInstance.getArgumentHandler().handle(context, argumentInstance));

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

        return this.tabCompleteCommand(sender, command, args);
    }

    private List<String> tabCompleteCommand(CommandSender sender, RoseCommand command, String[] args) {
        if (!command.canUse(sender) || (command.isPlayerOnly() && !(sender instanceof Player)))
            return Collections.emptyList();

        int argumentPosition = args.length - 2;
        if (argumentPosition >= command.getArgumentInfo().size()) {
            if (!command.hasSubCommand())
                return Collections.emptyList();

            int subCommandIndex = command.getSubCommandArgumentIndex();
            RoseSubCommand subCommand = this.getSubCommand(command, args[subCommandIndex + 1]);
            if (subCommand == null)
                return Collections.emptyList();

            String[] cmdArgs = new String[args.length - subCommandIndex - 1];
            System.arraycopy(args, subCommandIndex + 1, cmdArgs, 0, cmdArgs.length);

            return this.tabCompleteCommand(sender, subCommand, cmdArgs);
        }

        String[] cmdArgs = new String[args.length - 1];
        System.arraycopy(args, 1, cmdArgs, 0, cmdArgs.length);
        CommandContext context = new CommandContext(sender, cmdArgs);

        RoseCommandArgumentInfo argumentInfo = command.getArgumentInfo().get(argumentPosition);
        if (argumentInfo.isSubCommand())
            return command.getSubCommands().keySet()
                    .stream()
                    .filter(x -> StringUtil.startsWithIgnoreCase(x, cmdArgs[cmdArgs.length - 1]))
                    .collect(Collectors.toList());

        RoseCommandArgumentHandler<?> argumentHandler = this.resolveArgumentHandler(argumentInfo.getType());
        return argumentHandler.suggest(context, new ArgumentInstance(argumentInfo, argumentHandler, cmdArgs[cmdArgs.length - 1]))
                .stream()
                .filter(x -> StringUtil.startsWithIgnoreCase(x, cmdArgs[cmdArgs.length - 1]))
                .collect(Collectors.toList());
    }

}
