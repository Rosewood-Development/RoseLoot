package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Bat;

public class BatConditions extends EntityConditions {

    public BatConditions() {
        LootConditions.registerTag("bat-sleeping", context -> context.getLootedEntity() instanceof Bat && !((Bat) context.getLootedEntity()).isAwake());
    }

}
