package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import io.papermc.paper.text.Filtered;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class WritableBookContentComponent implements LootItemComponent {

    private final List<Filtered<String>> pages;

    public WritableBookContentComponent(ConfigurationSection section) {
        ConfigurationSection writableBookContentSection = section.getConfigurationSection("writable-book-content");
        if (writableBookContentSection != null) {
            this.pages = new ArrayList<>();
            if (writableBookContentSection.contains("pages")) {
                ConfigurationSection pagesSection = writableBookContentSection.getConfigurationSection("pages");
                if (pagesSection != null) {
                    for (String key : pagesSection.getKeys(false)) {
                        ConfigurationSection pageSection = pagesSection.getConfigurationSection(key);
                        if (pageSection != null) {
                            String raw = pageSection.getString("raw");
                            if (raw != null) {
                                String filtered = pageSection.getString("filtered");
                                this.pages.add(Filtered.of(raw, filtered));
                            }
                        }
                    }
                }
            }
        } else {
            this.pages = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.pages != null && !this.pages.isEmpty()) {
            WritableBookContent.Builder builder = WritableBookContent.writeableBookContent();
            builder.addFilteredPages(this.pages);
            itemStack.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT, builder.build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.WRITABLE_BOOK_CONTENT))
            return;

        WritableBookContent writableBookContent = itemStack.getData(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (writableBookContent.pages().isEmpty())
            return;
            
        stringBuilder.append("writable-book-content:\n");
        stringBuilder.append("  pages:\n");
        for (int i = 0; i < writableBookContent.pages().size(); i++) {
            Filtered<String> page = writableBookContent.pages().get(i);
            stringBuilder.append("    ").append(i).append(":\n");
            stringBuilder.append("      raw: ").append(page.raw()).append("\n");
            if (page.filtered() != null)
                stringBuilder.append("      filtered: ").append(page.filtered()).append("\n");
        }
    }

} 
