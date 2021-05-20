package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.LivingEntity;

public class FullyGrownCondition extends LootCondition {

    public FullyGrownCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        if (context.getLootedBlock() != null) {
            Block block = context.getLootedBlock();
            return block instanceof Ageable && ((Ageable) block).getAge() == ((Ageable) block).getMaximumAge();
        } else if (context.getLootedEntity() != null) {
            LivingEntity entity = context.getLootedEntity();
            return entity instanceof org.bukkit.entity.Ageable && ((org.bukkit.entity.Ageable) entity).isAdult();
        }
        return false;
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
