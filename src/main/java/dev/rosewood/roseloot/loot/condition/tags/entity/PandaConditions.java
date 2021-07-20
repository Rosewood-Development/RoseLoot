package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;

public class PandaConditions extends EntityConditions {

    public PandaConditions() {
        LootConditions.registerTag("panda-main-gene", PandaMainGeneCondition.class);
        LootConditions.registerTag("panda-hidden-gene", PandaHiddenGeneCondition.class);
    }

    public static class PandaMainGeneCondition extends LootCondition {

        private List<Panda.Gene> types;

        public PandaMainGeneCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Panda))
                return false;
            return this.types.contains(((Panda) looted).getMainGene());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Panda.Gene gene = Panda.Gene.valueOf(value.toUpperCase());
                    this.types.add(gene);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

    public static class PandaHiddenGeneCondition extends LootCondition {

        private List<Panda.Gene> types;

        public PandaHiddenGeneCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Panda))
                return false;
            return this.types.contains(((Panda) looted).getHiddenGene());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Panda.Gene gene = Panda.Gene.valueOf(value.toUpperCase());
                    this.types.add(gene);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

}
