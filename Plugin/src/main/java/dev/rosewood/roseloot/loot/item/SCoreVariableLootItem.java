package dev.rosewood.roseloot.loot.item;

import com.ssomar.score.variables.Variable;
import com.ssomar.score.variables.manager.VariablesManager;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SCoreVariableLootItem implements AutoTriggerableLootItem {

    private final String variable;
    private final boolean global;
    private final boolean set;
    private final NumberProvider amount;
    private final StringProvider value;

    protected SCoreVariableLootItem(String variable, boolean global, boolean set, NumberProvider amount, StringProvider value) {
        this.variable = variable;
        this.global = global;
        this.set = set;
        this.amount = amount;
        this.value = value;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        VariablesManager variablesManager = VariablesManager.getInstance();
        Optional<Variable> variableOptional = variablesManager.getVariable(this.variable);
        if (variableOptional.isEmpty()) {
            RoseLoot.getInstance().getLogger().warning("No SCore variable: " + this.variable);
            return;
        }

        Optional<Player> looterOptional = context.getLootingPlayer();
        if (!this.global && looterOptional.isEmpty())
            return;

        Variable variable = variableOptional.get();
        if (this.value == null) {
            double offsetAmount = this.amount.getDouble(context);
            Optional<String> result;
            if (this.set) {
                result = variable.setValue(this.global ? Optional.empty() : Optional.of(looterOptional.get()), String.valueOf(offsetAmount));
            } else {
                result = variable.modifValue(this.global ? Optional.empty() : Optional.of(looterOptional.get()), String.valueOf(offsetAmount));
            }
            result.ifPresent(x -> RoseLoot.getInstance().getLogger().warning("Couldn't modify SCore variable: " + x));
        } else {
            Optional<String> result = variable.setValue(this.global ? Optional.empty() : Optional.of(looterOptional.get()), this.value.get(context));
            result.ifPresent(x -> RoseLoot.getInstance().getLogger().warning("Couldn't modify SCore variable: " + x));
        }
    }

    public static SCoreVariableLootItem fromSection(ConfigurationSection section) {
        String variable = section.getString("variable");
        boolean global = section.getBoolean("global", false);
        boolean set = section.getBoolean("set", false);
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        StringProvider value = StringProvider.fromSection(section, "value", null);
        return new SCoreVariableLootItem(variable, global, set, amount, value);
    }

}
