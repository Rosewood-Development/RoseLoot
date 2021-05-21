package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;

public class PermissionCondition extends LootCondition {

    private String permission;

    public PermissionCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getLooter() != null && context.getLooter().hasPermission(this.permission);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        this.permission = values[0];
        return !this.permission.isEmpty();
    }

}
