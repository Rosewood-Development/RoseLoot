package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.hook.CoreProtectRecentBlockHook;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.List;
import java.util.Optional;
import net.coreprotect.CoreProtect;
import org.bukkit.block.Block;

public class CoreProtectNaturalBlockCondition extends BaseLootCondition {

    public CoreProtectNaturalBlockCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Block> optionalBlock = context.get(LootContextParams.LOOTED_BLOCK);
        if (optionalBlock.isEmpty())
            return false;

        Block block = optionalBlock.get();
        if (CoreProtectRecentBlockHook.isMarked(block))
            return false;

        List<String[]> history = CoreProtect.getInstance().getAPI().blockLookup(block, 0);
        return history == null || history.isEmpty();
    }

    @Override
    public boolean parseValues(String[] values) {
        return values.length == 0;
    }

}
