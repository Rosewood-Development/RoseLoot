package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@SuppressWarnings("deprecation")
public class BookItemLootMeta extends ItemLootMeta {

    private final StringProvider title;
    private final StringProvider author;
    private final StringProvider pages;
    private BookMeta.Generation generation;

    public BookItemLootMeta(ConfigurationSection section) {
        super(section);

        this.title = StringProvider.fromSection(section, "title", null);
        this.author = StringProvider.fromSection(section, "author", null);
        this.pages = StringProvider.fromSection(section, "pages", null);

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

        if (!(itemStack.getItemMeta() instanceof BookMeta itemMeta))
            return itemStack;

        if (this.title != null) itemMeta.setTitle(this.title.get(context));
        if (this.author != null) itemMeta.setAuthor(this.author.get(context));
        if (this.pages != null) {
            List<String> pages = this.pages.getListFormatted(context);
            if (!pages.isEmpty())
                itemMeta.spigot().setPages(pages.stream().map(TextComponent::fromLegacyText).toList());
        }
        if (this.generation != null) itemMeta.setGeneration(this.generation);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof BookMeta itemMeta))
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
