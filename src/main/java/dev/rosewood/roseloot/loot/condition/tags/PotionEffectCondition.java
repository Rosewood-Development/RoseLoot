package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectCondition extends LootCondition {

    private PotionEffectType potionEffectType;
    private int minLevel;

    public PotionEffectCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Entity entity = context.getLooter();
        if (!(entity instanceof LivingEntity))
            return false;

        LivingEntity livingEntity = (LivingEntity) entity;
        for (PotionEffect potionEffect : livingEntity.getActivePotionEffects())
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
