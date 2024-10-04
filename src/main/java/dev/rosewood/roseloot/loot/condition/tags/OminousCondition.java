package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.Optional;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.Vault;

public class OminousCondition extends BaseLootCondition {

    public OminousCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<BlockInfo> lootedBlock = context.getLootedBlockInfo();
        if (lootedBlock.isEmpty())
            return false;

        BlockInfo blockInfo = lootedBlock.get();
        BlockData blockData = blockInfo.getData();
        if (blockData instanceof Vault vault) {
            return vault.isOminous();
        } else if (blockData instanceof TrialSpawner trialSpawner) {
            return trialSpawner.isOminous();
        } else {
            return false;
        }
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
