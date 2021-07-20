package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Vex;

public class VexConditions extends EntityConditions {

    public VexConditions() {
        LootConditions.registerTag("vex-charging", context -> context.getLootedEntity() instanceof Vex && ((Vex) context.getLootedEntity()).isCharging());
    }

}
