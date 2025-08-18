package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RecipesComponent implements LootItemComponent {

    private final StringProvider recipes;

    public RecipesComponent(ConfigurationSection section) {
        this.recipes = StringProvider.fromSection(section, "recipes", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.recipes != null) {
            List<String> recipes = this.recipes.getList(context);
            if (recipes != null && !recipes.isEmpty()) {
                List<Key> recipeKeys = new ArrayList<>();
                for (String recipe : recipes) {
                    if (recipe != null && !recipe.isEmpty())
                        recipeKeys.add(Key.key(recipe.toLowerCase()));
                }
                if (!recipeKeys.isEmpty())
                    itemStack.setData(DataComponentTypes.RECIPES, recipeKeys);
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.RECIPES))
            return;

        List<Key> recipes = itemStack.getData(DataComponentTypes.RECIPES);
        if (!recipes.isEmpty()) {
            stringBuilder.append("recipes:\n");
            for (Key recipe : recipes)
                stringBuilder.append("  - '").append(recipe.asMinimalString()).append("'\n");
        }
    }

} 
