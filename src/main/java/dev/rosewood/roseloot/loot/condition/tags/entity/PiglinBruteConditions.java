package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.PiglinBrute;

public class PiglinBruteConditions extends EntityConditions {

    public PiglinBruteConditions() {
        LootConditions.registerTag("piglin-brute-converting", context -> context.getLootedEntity() instanceof PiglinBrute && ((PiglinBrute) context.getLootedEntity()).isConverting());
        LootConditions.registerTag("piglin-brute-immune-to-zombification", context -> context.getLootedEntity() instanceof PiglinBrute && ((PiglinBrute) context.getLootedEntity()).isImmuneToZombification());
    }

}
