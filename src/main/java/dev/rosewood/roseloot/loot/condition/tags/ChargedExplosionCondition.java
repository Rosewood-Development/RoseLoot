package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.entity.Creeper;

public class ChargedExplosionCondition extends LootCondition {

    public ChargedExplosionCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getLooter() instanceof Creeper && ((Creeper) context.getLooter()).isPowered();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
