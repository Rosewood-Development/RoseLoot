package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Optional;
import java.util.regex.Pattern;
import net.momirealms.customfishing.api.CustomFishingPlugin;
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

        String[] pieces = id.split(Pattern.quote(":"), 2);
        if (pieces.length != 2)
            return null;

        Optional<Player> optionalPlayer = context.getLootingPlayer();
        return CustomFishingPlugin.getInstance().getItemManager().build(optionalPlayer.orElse(null), pieces[0], pieces[1]);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return CustomFishingPlugin.getInstance().getItemManager().getCustomFishingItemID(item);
    }

}
