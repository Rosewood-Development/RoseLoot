package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Bee;

public class BeeConditions extends EntityConditions {

    public BeeConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("bee-angry", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Bee.class).filter(x -> x.getAnger() > 0).isPresent());
        event.registerLootCondition("bee-has-hive", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Bee.class).filter(x -> x.getHive() != null).isPresent());
        event.registerLootCondition("bee-has-stung", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Bee.class).filter(Bee::hasStung).isPresent());
        event.registerLootCondition("bee-has-flower", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Bee.class).filter(x -> x.getFlower() != null).isPresent());
        event.registerLootCondition("bee-has-nectar", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Bee.class).filter(Bee::hasNectar).isPresent());
    }

}
