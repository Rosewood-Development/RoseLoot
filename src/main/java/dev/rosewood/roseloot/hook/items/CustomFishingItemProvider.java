package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Optional;
import java.util.regex.Pattern;
import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.context.Context;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomFishingItemProvider extends ItemProvider {

    public CustomFishingItemProvider() {
        super("CustomFishing", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        Optional<Player> optionalPlayer = context.getLootingPlayer();
        return BukkitCustomFishingPlugin.getInstance().getItemManager().buildAny(Context.player(optionalPlayer.orElse(null)), id);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return BukkitCustomFishingPlugin.getInstance().getItemManager().getCustomFishingItemID(item);
    }

}
