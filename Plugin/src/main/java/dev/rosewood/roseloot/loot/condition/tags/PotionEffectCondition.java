package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Optional;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectCondition extends BaseLootCondition {

    private PotionEffectType potionEffectType;
    private int minLevel;

    public PotionEffectCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<LivingEntity> looter = context.getAs(LootContextParams.LOOTER, LivingEntity.class);
        if (looter.isEmpty())
            return false;

        for (PotionEffect potionEffect : looter.get().getActivePotionEffects())
            if (potionEffect.getType() == this.potionEffectType && potionEffect.getAmplifier() + 1 >= this.minLevel)
                return true;

        return false;
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length < 1)
            return false;

        try {
            this.potionEffectType = PotionEffectType.getByName(values[0]);
            this.minLevel = values.length > 1 ? Integer.parseInt(values[1]) : 0;
            return this.potionEffectType != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
