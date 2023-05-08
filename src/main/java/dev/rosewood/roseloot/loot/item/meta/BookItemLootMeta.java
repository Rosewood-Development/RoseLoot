package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@SuppressWarnings("deprecation")
public class BookItemLootMeta extends ItemLootMeta {

    private String title;
    private String author;
    private List<String> pages;
    private BookMeta.Generation generation;

    public BookItemLootMeta(ConfigurationSection section) {
        super(section);

        if (section.isString("title")) this.title = section.getString("title");
        if (section.isString("author")) this.author = section.getString("author");
        if (section.isList("pages")) this.pages = section.getStringList("pages");

        if (section.isString("generation")) {
            String generation = section.getString("generation");
            for (BookMeta.Generation value : BookMeta.Generation.values()) {
                if (value.name().equalsIgnoreCase(generation)) {
                    this.generation = value;
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        BookMeta itemMeta = (BookMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.title != null) itemMeta.setTitle(this.title);
        if (this.author != null) itemMeta.setAuthor(this.author);
        if (this.pages != null && !this.pages.isEmpty())
            itemMeta.spigot().setPages(this.pages.stream().map(context::formatText).map(TextComponent::fromLegacyText).toList());
        if (this.generation != null) itemMeta.setGeneration(this.generation);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        BookMeta itemMeta = (BookMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (itemMeta.hasTitle()) stringBuilder.append("title: ").append(LootUtils.decolorize(itemMeta.getTitle())).append('\n');
        if (itemMeta.hasAuthor()) stringBuilder.append("author: ").append(LootUtils.decolorize(itemMeta.getAuthor())).append('\n');
        if (itemMeta.hasPages()) {
            stringBuilder.append("pages:\n");
            for (String page : itemMeta.getPages())
                stringBuilder.append("  - '").append(LootUtils.decolorize(page)).append("'\n");
        }
        if (itemMeta.getGeneration() != null) stringBuilder.append("generation: ").append(itemMeta.getGeneration().name().toLowerCase()).append('\n');
    }

}
