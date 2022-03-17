package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.GlowSquid;

public class GlowSquidConditions extends EntityConditions {

    public GlowSquidConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("glow-squid-dark", context -> context.getAs(LootContextParams.LOOTED_ENTITY, GlowSquid.class).filter(x -> x.getDarkTicksRemaining() > 0).isPresent());
    }

}
