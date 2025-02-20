package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.regex.Pattern;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class CustomCraftingItemProvider extends ItemProvider {

    public CustomCraftingItemProvider() {
        super("CustomCrafting", false);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        String[] pieces = id.split(Pattern.quote(":"), 2);
        if (pieces.length != 2)
            return null;

        CustomItem customItem = CustomCrafting.inst().getApi().getRegistries().getCustomItems().get(new NamespacedKey(pieces[0], pieces[1]));
        if (customItem == null)
            return null;

        return customItem.create(1);
    }

}
