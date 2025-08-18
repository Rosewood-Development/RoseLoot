package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.util.ComponentUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import io.papermc.paper.text.Filtered;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class WrittenBookContentComponent implements LootItemComponent {

    private final Filtered<String> title;
    private final String author;
    private final int generation;
    private final List<Filtered<? extends ComponentLike>> pages;
    private final boolean resolved;

    public WrittenBookContentComponent(ConfigurationSection section) {
        ConfigurationSection writtenBookContentSection = section.getConfigurationSection("written-book-content");
        if (writtenBookContentSection != null) {
            // Parse title
            String rawTitle = writtenBookContentSection.getString("title");
            String filteredTitle = writtenBookContentSection.getString("filtered-title");
            this.title = rawTitle != null ? Filtered.of(rawTitle, filteredTitle) : null;

            // Parse author
            this.author = writtenBookContentSection.getString("author");

            // Parse generation (0-3)
            int generation = writtenBookContentSection.getInt("generation", 0);
            this.generation = Math.max(0, Math.min(3, generation));

            // Parse pages
            this.pages = new ArrayList<>();
            if (writtenBookContentSection.contains("pages")) {
                ConfigurationSection pagesSection = writtenBookContentSection.getConfigurationSection("pages");
                if (pagesSection != null) {
                    for (String key : pagesSection.getKeys(false)) {
                        ConfigurationSection pageSection = pagesSection.getConfigurationSection(key);
                        if (pageSection != null) {
                            String raw = pageSection.getString("raw");
                            if (raw != null) {
                                String filtered = pageSection.getString("filtered");
                                Component rawComponent = ComponentUtil.colorifyAndComponentify(raw);
                                Component filteredComponent = filtered != null ? ComponentUtil.colorifyAndComponentify(filtered) : null;
                                this.pages.add(Filtered.of(rawComponent, filteredComponent));
                            }
                        }
                    }
                }
            }

            // Parse resolved status
            this.resolved = writtenBookContentSection.getBoolean("resolved", false);
        } else {
            this.title = null;
            this.author = null;
            this.generation = 0;
            this.pages = null;
            this.resolved = false;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.title != null && this.author != null && this.pages != null && !this.pages.isEmpty()) {
            WrittenBookContent.Builder builder = WrittenBookContent.writtenBookContent(this.title, this.author);
            builder.generation(this.generation);
            builder.resolved(this.resolved);
            builder.addFilteredPages(this.pages);
            itemStack.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, builder.build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.WRITTEN_BOOK_CONTENT))
            return;

        WrittenBookContent writtenBookContent = itemStack.getData(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContent.pages().isEmpty())
            return;
            
        stringBuilder.append("written-book-content:\n");
        stringBuilder.append("  title: ").append(writtenBookContent.title().raw()).append("\n");
        if (writtenBookContent.title().filtered() != null)
            stringBuilder.append("  filtered-title: ").append(writtenBookContent.title().filtered()).append("\n");
        stringBuilder.append("  author: ").append(writtenBookContent.author()).append("\n");
        stringBuilder.append("  generation: ").append(writtenBookContent.generation()).append("\n");
        stringBuilder.append("  resolved: ").append(writtenBookContent.resolved()).append("\n");
        stringBuilder.append("  pages:\n");
        for (int i = 0; i < writtenBookContent.pages().size(); i++) {
            Filtered<Component> page = writtenBookContent.pages().get(i);
            stringBuilder.append("    ").append(i).append(":\n");
            stringBuilder.append("      raw: ").append(page.raw()).append("\n");
            if (page.filtered() != null)
                stringBuilder.append("      filtered: ").append(page.filtered()).append("\n");
        }
    }
} 
