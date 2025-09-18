package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;

public class LogCondition extends BaseLootCondition {

    private String message;

    public LogCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        context.getCurrentLootTable().ifPresent(x -> RoseLoot.getInstance().getLogger().info(String.format("[%s]: %s", x.getName(), this.message)));
        return true;
    }

    @Override
    public boolean parseValues(String[] values) {
        this.message = String.join(" ", values);
        return !this.message.isBlank();
    }

}
