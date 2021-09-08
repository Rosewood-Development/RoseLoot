package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.GlowSquid;

public class GlowSquidConditions extends EntityConditions {

    public GlowSquidConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("glow-squid-dark", context -> context.getLootedEntity() instanceof GlowSquid && ((GlowSquid) context.getLootedEntity()).getDarkTicksRemaining() > 0);
    }

}
