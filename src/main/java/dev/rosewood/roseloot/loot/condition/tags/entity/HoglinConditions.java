package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Hoglin;

public class HoglinConditions extends EntityConditions {

    public HoglinConditions() {
        LootConditions.registerTag("hoglin-unhuntable", context -> context.getLootedEntity() instanceof Hoglin && !((Hoglin) context.getLootedEntity()).isAbleToBeHunted());
    }

}
