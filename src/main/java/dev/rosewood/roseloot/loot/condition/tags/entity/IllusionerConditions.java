package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.Spellcaster;

public class IllusionerConditions extends EntityConditions {

    public IllusionerConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("illusioner-spell", CatConditions.CatTypeCondition.class);
    }

    public static class IllusionerSpellCondition extends LootCondition {

        private List<Spellcaster.Spell> types;

        public IllusionerSpellCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Illusioner.class)
                    .map(Illusioner::getSpell)
                    .filter(this.types::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Spellcaster.Spell spell = Spellcaster.Spell.valueOf(value.toUpperCase());
                    this.types.add(spell);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

}
