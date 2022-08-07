package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.regex.Pattern;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
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

        CustomRecipe<?> recipe = CustomCrafting.inst().getRegistries().getRecipes().get(new NamespacedKey(pieces[0], pieces[1]));
        if (recipe == null)
            return null;

        return recipe.getResult().getItemStack();
    }

}
