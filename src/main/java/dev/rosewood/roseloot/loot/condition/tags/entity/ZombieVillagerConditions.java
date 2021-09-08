package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

public class ZombieVillagerConditions extends EntityConditions {

    public ZombieVillagerConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("zombie-villager-converting", context -> context.getLootedEntity() instanceof ZombieVillager && ((ZombieVillager) context.getLootedEntity()).isConverting());
        event.registerLootCondition("zombie-villager-profession", ZombieVillagerProfessionCondition.class);
    }

    public static class ZombieVillagerProfessionCondition extends LootCondition {

        private List<Villager.Profession> professions;

        public ZombieVillagerProfessionCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof ZombieVillager))
                return false;
            return this.professions.contains(((ZombieVillager) looted).getVillagerProfession());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.professions = new ArrayList<>();

            for (String value : values) {
                try {
                    Villager.Profession profession = Villager.Profession.valueOf(value.toUpperCase());
                    this.professions.add(profession);
                } catch (Exception ignored) { }
            }

            return !this.professions.isEmpty();
        }

    }

}
