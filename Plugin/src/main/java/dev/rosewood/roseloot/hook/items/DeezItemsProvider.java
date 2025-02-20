package dev.rosewood.roseloot.hook.items;

import dev.luminescent.deezitems.DeezItems;
import dev.luminescent.deezitems.manager.ItemManager;
import dev.luminescent.deezitems.manager.MaterialManager;
import dev.luminescent.deezitems.utils.DeezItem;
import dev.luminescent.deezitems.utils.DeezMaterial;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public class DeezItemsProvider extends ItemProvider {

    public DeezItemsProvider() {
        super("DeezItems", false);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        DeezItem item = DeezItems.getInstance().getManager(ItemManager.class).getItem(id);
        if (item != null)
            return item.generate(1);

        DeezMaterial material = DeezItems.getInstance().getManager(MaterialManager.class).getMaterial(id);
        if (material != null)
            return material.generate(1);

        return null;
    }

}
