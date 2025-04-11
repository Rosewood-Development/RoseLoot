package dev.rosewood.roseloot.loot.item;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class SkriptItemLootItem implements ItemGenerativeLootItem {

    Function<?> function;
    Object[][] parameters;
    String params;
    private final String functionName;

    protected SkriptItemLootItem(String functionName, String params) {
        this.functionName = functionName;
        this.params = params;
    }

    @Override
    public List<ItemStack> generate(LootContext context) {
        function = Functions.getGlobalFunction(functionName);
        if (function == null) {
            RoseLoot.getInstance().getLogger().warning("Skript function " + functionName + " does not exist!");
            return List.of();
        }
        if (function.getReturnType() == null || !function.getReturnType().getCodeName().equals("itemstack")) {
            RoseLoot.getInstance().getLogger().warning("Skript function " + functionName + " does not return an itemstack!");
            return List.of();
        }

        // Fill parameters
        parameters = new Object[function.getParameters().length][];
        for(int i=0;i<function.getParameters().length;i++) {
            String type = function.getParameter(i).getType().getCodeName();
            String name = function.getParameter(i).getName();
            if (type.equals("player") && name.equals("player") && context.getLootingPlayer().isPresent()) {
                parameters[i] = new Player[] {context.getLootingPlayer().get()};
            }
            else if (type.equals("string") && name.equals("params")) {
                parameters[i] = new String[] {params};
            }
            else if (type.equals("object") && name.equals("context")) {
                parameters[i] = new LootContext[] {context};
            }
            else {
                parameters[i] = new Object[] {null};
            }
        }

        Object[] execute = function.execute(parameters);
        return Optional.ofNullable(execute).stream()
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(ItemStack.class::isInstance)
                .map(ItemStack.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        return this.generate(context);
    }

    public static SkriptItemLootItem fromSection(ConfigurationSection section) {
        String functionName = section.getString("function");
        if (functionName == null) return null;
        String params = section.getString("params", "");
        return new SkriptItemLootItem(functionName, params);
    }
}
