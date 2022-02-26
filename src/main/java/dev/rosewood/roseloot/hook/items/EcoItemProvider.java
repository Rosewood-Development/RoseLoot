package dev.rosewood.roseloot.hook.items;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class EcoItemProvider implements ItemProvider {

    private Method lookupMethod, getItemMethod;

    public EcoItemProvider() {
        if (!Bukkit.getPluginManager().isPluginEnabled("eco"))
            return;

        try {
            Class<?> itemClass = Class.forName("com.willfp.eco.core.items.Items");
            this.lookupMethod = itemClass.getMethod("lookup", String.class);
            Class<?> testableItemClass = Class.forName("com.willfp.eco.core.items.TestableItem");
            this.getItemMethod = testableItemClass.getMethod("getItem");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack getItem(String id) {
        if (this.lookupMethod == null || this.getItemMethod == null)
            return null;

        try {
            Object testableItem = this.lookupMethod.invoke(null, id);
            if (testableItem == null)
                return null;

            return (ItemStack) this.getItemMethod.invoke(testableItem);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
