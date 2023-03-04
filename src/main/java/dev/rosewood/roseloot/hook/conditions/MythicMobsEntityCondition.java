package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import io.lumine.mythic.bukkit.MythicBukkit;

public class MythicMobsEntityCondition extends LootCondition {

    public MythicMobsEntityCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.LOOTED_ENTITY)
                .filter(x -> MythicBukkit.inst().getAPIHelper().getMythicMobInstance(x) != null)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
