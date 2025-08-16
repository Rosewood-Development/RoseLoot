package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.BlockInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.block.data.type.Vault;

public class VaultStateCondition extends BaseLootCondition {

    private List<Vault.State> states;

    public VaultStateCondition(String tag) {
        super(tag);
    }

    @SuppressWarnings("removal") // using correct method per version, will use reflection after removal
    @Override
    public boolean check(LootContext context) {
        Optional<BlockInfo> lootedBlock = context.getLootedBlockInfo();
        if (lootedBlock.isEmpty())
            return false;

        BlockInfo blockInfo = lootedBlock.get();
        if (!(blockInfo.getData() instanceof Vault vault))
            return false;

        if (NMSUtil.getVersionNumber() > 21 || (NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3)) {
            return this.states.contains(vault.getVaultState());
        } else {
            return this.states.contains(vault.getTrialSpawnerState());
        }
    }

    @Override
    public boolean parseValues(String[] values) {
        this.states = new ArrayList<>();

        for (String value : values) {
            try {
                Vault.State state = Vault.State.valueOf(value.toUpperCase());
                this.states.add(state);
            } catch (Exception ignored) { }
        }

        return !this.states.isEmpty();
    }

}
