package dev.rosewood.roseloot.loot.item;

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
import svar.ajneb97.managers.PlayerVariablesManager;
import svar.ajneb97.managers.ServerVariablesManager;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.VariableType;

public class ServerVariableLootItem implements AutoTriggerableLootItem {

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

        switch (this.variableType) {
            case GLOBAL -> {
                ServerVariablesManager variablesManager = plugin.getServerVariablesManager();
                switch (this.valueOperation) {
                    case SET -> variablesManager.setVariable(this.variable, this.getValue(context));
                    case ADD -> variablesManager.modifyVariable(this.variable, this.getValue(context), true);
                    case REDUCE -> variablesManager.modifyVariable(this.variable, this.getValue(context), false);
                    case RESET -> variablesManager.resetVariable(this.variable);
                }
            }
            case PLAYER -> {
                Optional<Player> lootingPlayer = context.getLootingPlayer();
                if (lootingPlayer.isEmpty())
                    return;

                Player player = lootingPlayer.get();
                PlayerVariablesManager variablesManager = plugin.getPlayerVariablesManager();
                switch (this.valueOperation) {
                    case SET -> variablesManager.setVariable(player.getName(), this.variable, this.getValue(context));
                    case ADD -> variablesManager.modifyVariable(player.getName(), this.variable, this.getValue(context), true);
                    case REDUCE -> variablesManager.modifyVariable(player.getName(), this.variable, this.getValue(context), false);
                    case RESET -> variablesManager.resetVariable(this.variable, player.getName(), false);
                }
            }
        }
    }

    private String getValue(LootContext context) {
        return switch (this.valueType) {
            case TEXT -> this.value.get(context);
            case INTEGER -> String.valueOf(this.amount.getInteger(context));
            case DOUBLE -> String.valueOf(this.amount.getDouble(context));
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
