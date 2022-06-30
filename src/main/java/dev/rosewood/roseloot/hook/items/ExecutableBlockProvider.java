package dev.rosewood.roseloot.hook.items;

import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public class ExecutableBlockProvider extends ItemProvider {

    public ExecutableBlockProvider() {
        super("ExecutableBlocks", false);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        return ExecutableBlocksAPI.getExecutableBlocksManager()
                .getExecutableBlock(id)
                .map(x -> x.buildItem(1, context.getLootingPlayer()))
                .orElse(null);
    }

}
