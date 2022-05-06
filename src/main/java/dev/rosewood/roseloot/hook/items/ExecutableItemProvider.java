package dev.rosewood.roseloot.hook.items;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public class ExecutableItemProvider extends ItemProvider {

    public ExecutableItemProvider() {
        super("ExecutableItems");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.isEnabled())
            return null;

        return ExecutableItemsAPI.getExecutableItemsManager()
                .getExecutableItem(id)
                .map(x -> x.buildItem(1, Optional.empty()))
                .orElse(null);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return ExecutableItemsAPI.getExecutableItemsManager()
                .getExecutableItem(item)
                .map(ExecutableItemInterface::getId)
                .orElse(null);
    }

}
