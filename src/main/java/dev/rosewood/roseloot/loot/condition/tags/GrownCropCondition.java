package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.BlockInfo;
import org.bukkit.block.data.Ageable;

public class GrownCropCondition extends BaseLootCondition {

    public GrownCropCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.getLootedBlockInfo()
                .map(BlockInfo::getData)
                .map(x -> x instanceof Ageable ? (Ageable) x : null)
                .filter(x -> x.getAge() == x.getMaximumAge())
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
