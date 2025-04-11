package dev.rosewood.roseloot.loot.item;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SkriptFunctionLootItem implements TriggerableLootItem {

    Function<?> function;
    Object[][] parameters;
    String params;
    private final String functionName;

    protected SkriptFunctionLootItem(String functionName, String params) {
        this.functionName = functionName;
        this.params = params;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Player> player = context.getLootingPlayer();
        function = Functions.getGlobalFunction(functionName);
        if (function == null) {
            RoseLoot.getInstance().getLogger().warning("Skript function " + functionName + " does not exist!");
            return;
        }
        parameters = new Object[function.getParameters().length][];

        // Fill parameters
        for(int i=0;i<function.getParameters().length;i++) {
            String type = function.getParameter(i).getType().getCodeName();
            String name = function.getParameter(i).getName();
            if (type.equals("player") && name.equals("player") && player.isPresent()) {
                parameters[i] = new Player[] {player.get()};
            }
            else if (type.equals("location") && name.equals("location")) {
                parameters[i] = new Location[] {location};
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

        // Execute function
        function.execute(parameters);
    }

    public static SkriptFunctionLootItem fromSection(ConfigurationSection section) {
        String functionName = section.getString("function");
        if (functionName == null) return null;
        String params = section.getString("params", "");
        return new SkriptFunctionLootItem(functionName, params);
    }
}
