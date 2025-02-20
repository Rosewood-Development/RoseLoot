package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

public class KnowledgeBookItemLootMeta extends ItemLootMeta {

    private final List<NamespacedKey> recipes;

    public KnowledgeBookItemLootMeta(ConfigurationSection section) {
        super(section);

        if (!section.contains("recipes")) {
            // No recipes section, just add all of them to the book
            this.recipes = new ArrayList<>();
            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe recipe = iterator.next();
                if (recipe instanceof Keyed)
                    this.recipes.add(((Keyed) recipe).getKey());
            }
        } else {
            this.recipes = section.getStringList("recipes").stream()
                    .map(String::toLowerCase)
                    .map(NamespacedKey::minecraft)
                    .toList();
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        KnowledgeBookMeta itemMeta = (KnowledgeBookMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (!this.recipes.isEmpty()) itemMeta.setRecipes(this.recipes);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        KnowledgeBookMeta itemMeta = (KnowledgeBookMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (itemMeta.hasRecipes()) {
            stringBuilder.append("recipes:\n");
            for (NamespacedKey recipe : itemMeta.getRecipes())
                stringBuilder.append("  - '").append(recipe).append("'\n");
        }
    }

}
