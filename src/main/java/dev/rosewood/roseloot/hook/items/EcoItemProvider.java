package dev.rosewood.roseloot.hook.items;

import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

/**
 * EcoItems support is added through reflection since it requires Java 17, and we want to keep compatibility with Java 8.
 */
public class EcoItemProvider extends ItemProvider {

    private Method lookupMethod, getItemMethod;
    private Method getCustomItemMethod, getKeyMethod;

    public EcoItemProvider() {
        super("eco");
    }

    @Override
    protected boolean checkEnabled(String pluginName) {
        if (!super.checkEnabled(pluginName))
            return false;

        try {
            Class<?> itemClass = Class.forName("com.willfp.eco.core.items.Items");
            this.lookupMethod = itemClass.getMethod("lookup", String.class);
            Class<?> testableItemClass = Class.forName("com.willfp.eco.core.items.TestableItem");
            this.getItemMethod = testableItemClass.getMethod("getItem");

            this.getCustomItemMethod = itemClass.getMethod("getCustomItem", ItemStack.class);
            Class<?> customItemClass = Class.forName("com.willfp.eco.core.items.CustomItem");
            this.getKeyMethod = customItemClass.getMethod("getKey");
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.isEnabled())
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

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        try {
            Object customItem = this.getCustomItemMethod.invoke(null, item);
            if (customItem == null)
                return null;

            return (String) this.getKeyMethod.invoke(customItem);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
