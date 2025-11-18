package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.EnumHelper;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.VariablesManager;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;

public class ServerVariableLootItem implements TriggerableLootItem {

    private final String variable;
    private final VariableType variableType;
    private final ValueType valueType;
    private final ValueOperation valueOperation;
    private final NumberProvider amount;
    private final StringProvider value;

    protected ServerVariableLootItem(String variable, VariableType variableType, ValueType valueType, ValueOperation valueOperation, NumberProvider amount, StringProvider value) {
        this.variable = variable;
        this.variableType = variableType;
        this.valueType = valueType;
        this.valueOperation = valueOperation;
        this.amount = amount;
        this.value = value;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        ServerVariables plugin = JavaPlugin.getPlugin(ServerVariables.class);
        VariablesManager variablesManager = plugin.getVariablesManager();

        Variable serverVariable = variablesManager.getVariable(this.variable);
        if (serverVariable == null) {
            RoseLoot.getInstance().getLogger().warning("Unknown ServerVariables variable: " + this.variable);
            return;
        }

        if (serverVariable.getValueType() == ValueType.LIST) {
            RoseLoot.getInstance().getLogger().warning("List variables from ServerVariables are not supported by RoseLoot. Unable to modify " + this.variable);
            return;
        }

        switch (this.variableType) {
            case GLOBAL -> {
                switch (this.valueOperation) {
                    case SET -> variablesManager.setVariableValue(null, this.variable, this.getValue(context));
                    case ADD -> variablesManager.modifyVariable(null, this.variable, this.getValue(context), true);
                    case REDUCE -> variablesManager.modifyVariable(null, this.variable, this.getValue(context), false);
                    case RESET -> variablesManager.resetVariable(null, this.variable, false);
                }
            }
            case PLAYER -> {
                Optional<Player> lootingPlayer = context.getLootingPlayer();
                if (lootingPlayer.isEmpty())
                    return;

                Player player = lootingPlayer.get();
                switch (this.valueOperation) {
                    case SET -> variablesManager.setVariableValue(player.getName(), this.variable, this.getValue(context));
                    case ADD -> variablesManager.modifyVariable(player.getName(), this.variable, this.getValue(context), true);
                    case REDUCE -> variablesManager.modifyVariable(player.getName(), this.variable, this.getValue(context), false);
                    case RESET -> variablesManager.resetVariable(player.getName(), this.variable, false);
                }
            }
        }
    }

    private String getValue(LootContext context) {
        return switch (this.valueType) {
            case TEXT -> this.value.get(context);
            case INTEGER -> String.valueOf(this.amount.getInteger(context));
            case DOUBLE -> String.valueOf(this.amount.getDouble(context));
            case LIST -> throw new IllegalStateException("List variables are not supported");
        };
    }

    public static ServerVariableLootItem fromSection(ConfigurationSection section) {
        String variable = section.getString("variable");
        VariableType variableType = EnumHelper.valueOf(VariableType.class, section.getString("variable-type", "player"));
        ValueType valueType = EnumHelper.valueOf(ValueType.class, section.getString("value-type", "integer"));
        ValueOperation valueOperation = EnumHelper.valueOf(ValueOperation.class, section.getString("value-operation", "add"));
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        StringProvider value = StringProvider.fromSection(section, "value", null);
        return new ServerVariableLootItem(variable, variableType, valueType, valueOperation, amount, value);
    }

    protected enum ValueOperation {
        SET,
        ADD,
        REDUCE,
        RESET
    }

}
