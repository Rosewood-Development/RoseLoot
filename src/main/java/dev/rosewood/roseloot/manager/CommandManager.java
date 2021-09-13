package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.ClassUtils;
import dev.rosewood.roseloot.command.argument.EnumArgumentHandler;
import dev.rosewood.roseloot.command.framework.ArgumentInstance;
import dev.rosewood.roseloot.command.framework.CommandContext;
import dev.rosewood.roseloot.command.framework.RoseCommand;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.roseloot.command.framework.RoseCommandArgumentInfo;
import java.util.ArrayList;
import java.util.Collections;
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

    private final Map<Class<? extends RoseCommandArgumentHandler>, RoseCommandArgumentHandler<?>> argumentHandlers;
    private final Map<String, RoseCommand> commandLookupMap;

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.argumentHandlers = new HashMap<>();
        this.commandLookupMap = new HashMap<>();
    }

    @Override
    public void reload() {
        try {
            // Load commands
            for (Class<RoseCommand> commandClass : ClassUtils.getClassesOf(this.rosePlugin, "dev.rosewood.roseloot.command.command", RoseCommand.class)) {
                RoseCommand command = commandClass.getConstructor(RosePlugin.class).newInstance(this.rosePlugin);
                this.commandLookupMap.put(command.getName().toLowerCase(), command);
                command.getAliases().forEach(x -> this.commandLookupMap.put(x.toLowerCase(), command));
            }

            // Load arguments
            for (Class<RoseCommandArgumentHandler> argumentHandlerClass : ClassUtils.getClassesOf(this.rosePlugin, "dev.rosewood.roseloot.command.argument", RoseCommandArgumentHandler.class)) {
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

        return this.argumentHandlers.values()
                .stream()
                .filter(x -> x.getHandledType() != null && x.getHandledType() == handledParameterClass)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public RoseCommand getCommand(String commandName) {
        return this.commandLookupMap.get(commandName);
    }

    public List<RoseCommand> getCommands() {
        return this.commandLookupMap.values().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        try {
            if (args.length == 0) {
                String baseColor = localeManager.getLocaleMessage("base-command-color");
                localeManager.sendCustomMessage(sender, baseColor + "Running <g:#8A2387:#E94057:#F27121>RoseLoot" + baseColor + " v" + this.rosePlugin.getDescription().getVersion());
                localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41e0f0:#ff8dce>" + this.rosePlugin.getDescription().getAuthors().get(0));
                localeManager.sendSimpleMessage(sender, "base-command-help");
                return true;
            }

            RoseCommand command = this.getCommand(args[0]);
            if (command == null) {
                localeManager.sendCustomMessage(sender, "&cUnknown command, use &b/rl help &cfor more info.");
                return true;
            }

            if (!command.canUse(sender)) {
                localeManager.sendMessage(sender, "no-permission");
                return true;
            }

            if (command.isPlayerOnly() && !(sender instanceof Player)) {
                localeManager.sendMessage(sender, "only-player");
                return true;
            }

            if (command.getNumRequiredArguments() > args.length - 1) {
                localeManager.sendCustomMessage(sender, "&cMissing arguments, " + command.getNumRequiredArguments() + " required.");
                return true;
            }

            String[] cmdArgs = new String[args.length - 1];
            System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
            CommandContext context = new CommandContext(sender, cmdArgs);

            List<RoseCommandArgumentInfo> argumentInfo = command.getArgumentInfo();
            List<ArgumentInstance> combinedArguments = new ArrayList<>();
            for (int i = 0; i < argumentInfo.size(); i++) {
                RoseCommandArgumentInfo argInfo = argumentInfo.get(i);
                String input = i < cmdArgs.length ? cmdArgs[i] : "";
                combinedArguments.add(new ArgumentInstance(argInfo, this.resolveArgumentHandler(argInfo.getType()), input));
            }

            List<ArgumentInstance> invalidArgs = combinedArguments.stream()
                    .filter(x -> x.getArgumentHandler().isInvalid(context, x.getArgument(), x))
                    .collect(Collectors.toList());

            if (!invalidArgs.isEmpty()) {
                localeManager.sendCustomMessage(sender, "&cInvalid argument(s), please correct your mistakes and try again...");
                return true;
            }

            Stream.Builder<Object> argumentBuilder = Stream.builder().add(context);
            for (ArgumentInstance argumentInstance : combinedArguments)
                argumentBuilder.add(argumentInstance.getArgumentHandler().handle(context, argumentInstance));

            command.getExecuteMethod().invoke(command, argumentBuilder.build().toArray());
        } catch (Exception e) {
            e.printStackTrace();
            localeManager.sendCustomMessage(sender, "&cAn unknown error occurred; details have been printed to console. Please contact a server administrator.");
        }
        return true;
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

        if (!command.canUse(sender))
            return Collections.emptyList();

        int argumentPosition = args.length - 2;
        if (argumentPosition >= command.getArgumentInfo().size())
            return Collections.emptyList();

        String[] cmdArgs = new String[args.length - 1];
        System.arraycopy(args, 1, cmdArgs, 0, args.length - 1);
        CommandContext context = new CommandContext(sender, cmdArgs);

        RoseCommandArgumentInfo argumentInfo = command.getArgumentInfo().get(argumentPosition);
        RoseCommandArgumentHandler<?> argumentHandler = this.resolveArgumentHandler(argumentInfo.getType());
        return argumentHandler.suggest(context, new ArgumentInstance(argumentInfo, argumentHandler, cmdArgs[cmdArgs.length - 1]))
                .stream()
                .filter(x -> StringUtil.startsWithIgnoreCase(x, cmdArgs[cmdArgs.length - 1]))
                .collect(Collectors.toList());
    }

}
