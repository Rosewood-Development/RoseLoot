package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Panda;

public class PandaConditions extends EntityConditions {

    public PandaConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("panda-main-gene", PandaMainGeneCondition.class);
        event.registerLootCondition("panda-hidden-gene", PandaHiddenGeneCondition.class);
    }

    public static class PandaMainGeneCondition extends LootCondition {

        private List<Panda.Gene> types;

        public PandaMainGeneCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Panda.class)
                    .map(Panda::getMainGene)
                    .filter(this.types::contains)
                    .isPresent();
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
            return context.getAs(LootContextParams.LOOTED_ENTITY, Panda.class)
                    .map(Panda::getHiddenGene)
                    .filter(this.types::contains)
                    .isPresent();
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
