package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.GlowSquid;

public class GlowSquidConditions extends EntityConditions {

    public GlowSquidConditions() {
        LootConditions.registerTag("glow-squid-dark", context -> context.getLootedEntity() instanceof GlowSquid && ((GlowSquid) context.getLootedEntity()).getDarkTicksRemaining() > 0);
    }

}
