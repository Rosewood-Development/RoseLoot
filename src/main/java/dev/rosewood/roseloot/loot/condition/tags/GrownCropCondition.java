package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;

public class GrownCropCondition extends BaseLootCondition {

    public GrownCropCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.LOOTED_BLOCK)
                .map(Block::getBlockData)
                .map(x -> x instanceof Ageable ? (Ageable) x : null)
                .filter(x -> x.getAge() == x.getMaximumAge())
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
