package dev.rosewood.roseloot.hook.items;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import org.bukkit.inventory.ItemStack;

public class MMOItemProvider extends ItemProvider {

    public MMOItemProvider() {
        super("MMOItems");
    }

    @Override
    public ItemStack getItem(String id) {
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

        NBTItem nbtItem = NBTItem.get(item);
        if (nbtItem == null)
            return null;

        VolatileMMOItem mmoItem = new VolatileMMOItem(nbtItem);
        String id = mmoItem.getId();
        if (id == null)
            return null;

        return type.getId() + ":" + id;
    }

}
