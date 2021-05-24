package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.roseloot.loot.LootContext;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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
        super.apply(itemStack, context);

        BookMeta itemMeta = (BookMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.title != null) itemMeta.setTitle(this.title);
        if (this.author != null) itemMeta.setAuthor(this.author);
        if (this.pages != null && !this.pages.isEmpty()) itemMeta.spigot().setPages(this.pages.stream().map(HexUtils::colorify).map(TextComponent::fromLegacyText).collect(Collectors.toList()));
        if (this.generation != null) itemMeta.setGeneration(this.generation);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
