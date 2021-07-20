package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Piglin;

public class PiglinConditions extends EntityConditions {

    public PiglinConditions() {
        LootConditions.registerTag("piglin-converting", context -> context.getLootedEntity() instanceof Piglin && ((Piglin) context.getLootedEntity()).isConverting());
        LootConditions.registerTag("piglin-immune-to-zombification", context -> context.getLootedEntity() instanceof Piglin && ((Piglin) context.getLootedEntity()).isImmuneToZombification());
        LootConditions.registerTag("piglin-unable-to-hunt", context -> context.getLootedEntity() instanceof Piglin && !((Piglin) context.getLootedEntity()).isAbleToHunt());
    }

}
