package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.block.data.type.TrialSpawner;

public class TrialSpawnerStateCondition extends BaseLootCondition {

    private List<TrialSpawner.State> states;

    public TrialSpawnerStateCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<BlockInfo> lootedBlock = context.getLootedBlockInfo();
        if (lootedBlock.isEmpty())
            return false;

        BlockInfo blockInfo = lootedBlock.get();
        if (!(blockInfo.getData() instanceof TrialSpawner vault))
            return false;

        return this.states.contains(vault.getTrialSpawnerState());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.states = new ArrayList<>();

        for (String value : values) {
            try {
                TrialSpawner.State state = TrialSpawner.State.valueOf(value.toUpperCase());
                this.states.add(state);
            } catch (Exception ignored) { }
        }

        return !this.states.isEmpty();
    }

}
