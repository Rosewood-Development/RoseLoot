package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

public class VillagerConditions extends EntityConditions {

    public VillagerConditions() {
        LootConditions.registerTag("villager-profession", VillagerProfessionCondition.class);
        LootConditions.registerTag("villager-type", VillagerTypeCondition.class);
        if (NMSUtil.getVersionNumber() >= 14)
            LootConditions.registerTag("villager-level", VillagerLevelCondition.class);
    }

    public static class VillagerProfessionCondition extends LootCondition {

        private List<Villager.Profession> professions;

        public VillagerProfessionCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Villager))
                return false;
            return this.professions.contains(((Villager) looted).getProfession());
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

    public static class VillagerTypeCondition extends LootCondition {

        private List<Villager.Type> types;

        public VillagerTypeCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Villager))
                return false;
            return this.types.contains(((Villager) looted).getVillagerType());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Villager.Type type = Villager.Type.valueOf(value.toUpperCase());
                    this.types.add(type);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

    public static class VillagerLevelCondition extends LootCondition {

        private int level;

        public VillagerLevelCondition(String tag) {
            super(tag);
        }

        @Override
        protected boolean checkInternal(LootContext context) {
            return context.getLootedEntity() instanceof Villager && ((Villager) context.getLootedEntity()).getVillagerLevel() >= this.level;
        }

        @Override
        public boolean parseValues(String[] values) {
            this.level = -1;

            try {
                this.level = Integer.parseInt(values[0]);
            } catch (Exception ignored) { }

            return this.level >= 0;
        }

    }

}
