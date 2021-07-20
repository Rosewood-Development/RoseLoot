package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spellcaster;

public class IllusionerConditions extends EntityConditions {

    public IllusionerConditions() {
        LootConditions.registerTag("illusioner-spell", CatConditions.CatTypeCondition.class);
    }

    public static class IllusionerSpellCondition extends LootCondition {

        private List<Spellcaster.Spell> types;

        public IllusionerSpellCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Illusioner))
                return false;
            return this.types.contains(((Illusioner) looted).getSpell());
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
