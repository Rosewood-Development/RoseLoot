package dev.rosewood.roseloot.hook.items;

import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public class ExecutableBlockProvider extends ItemProvider {

    public ExecutableBlockProvider() {
        super("ExecutableBlocks", false);
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.isEnabled())
            return null;

        return ExecutableBlocksAPI.getExecutableBlocksManager()
                .getExecutableBlock(id)
                .map(x -> x.buildItem(1, Optional.empty()))
                .orElse(null);
    }

}
