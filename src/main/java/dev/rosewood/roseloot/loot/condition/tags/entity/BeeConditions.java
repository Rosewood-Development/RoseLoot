package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Bee;

public class BeeConditions extends EntityConditions {

    public BeeConditions() {
        LootConditions.registerTag("bee-angry", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).getAnger() > 0);
        LootConditions.registerTag("bee-has-hive", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).getHive() != null);
        LootConditions.registerTag("bee-has-stung", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).hasStung());
        LootConditions.registerTag("bee-has-flower", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).getFlower() != null);
        LootConditions.registerTag("bee-has-nectar", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).hasNectar());
    }

}
