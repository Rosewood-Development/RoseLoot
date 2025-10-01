package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.HashSet;
import java.util.Set;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

public class MMOItemProvider extends ItemProvider {

    public MMOItemProvider() {
        super("MMOItems", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        String[] parts = id.split(":", 2);
        if (parts.length != 2)
            return null;

        return MMOItems.plugin.getItem(parts[0], parts[1]);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        Type type = Type.get(item);
        if (type == null)
            return null;

        String typeId = MMOItems.getTypeName(item);
        String itemId = MMOItems.getID(item);

        if (typeId != null && itemId != null) {
            return typeId + ":" + itemId;
        } else {
            return itemId;
        }
    }

    @Override
    public Set<String> getItemIds(ItemStack item) {
        if (!this.isEnabled())
            return Set.of();

        String typeId = MMOItems.getTypeName(item);
        String itemId = MMOItems.getID(item);

        Set<String> ids = new HashSet<>();
        if (typeId != null) {
            ids.add(typeId);
            if (itemId != null)
                ids.add(typeId + ":" + itemId);
        } else {
            if (itemId != null)
                ids.add(itemId);
        }

        return ids;
    }

}
