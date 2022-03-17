package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;

public class PermissionCondition extends LootCondition {

    private String permission;

    public PermissionCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.LOOTER)
                .filter(x -> x.hasPermission(this.permission))
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        this.permission = values[0];
        return !this.permission.isEmpty();
    }

}
