package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class BundleContentsComponent implements LootItemComponent {

    private final List<LootItem> contents;

    public BundleContentsComponent(ConfigurationSection section) {
        ConfigurationSection bundleContentsSection = section.getConfigurationSection("bundle-contents");
        if (bundleContentsSection != null) {
            this.contents = new ArrayList<>();
            for (String key : bundleContentsSection.getKeys(false)) {
                ConfigurationSection contentSection = bundleContentsSection.getConfigurationSection(key);
                if (contentSection != null) {
                    LootItem lootItem = RoseLoot.getInstance().getManager(LootTableManager.class).parseLootItem("$internal", "none", "bundle", key, contentSection);
                    if (lootItem != null) {
                        this.contents.add(lootItem);
                    } else {
                        RoseLoot.getInstance().getLogger().warning("Ignoring invalid bundle item: " + key);
                    }
                }
            }
        } else {
            this.contents = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        BundleContents.Builder builder = BundleContents.bundleContents();

        if (this.contents != null) {
            LootContents lootContents = new LootContents(context);
            lootContents.add(this.contents);
            List<ItemStack> items = LootUtils.combineSimilarItems(lootContents.getItems());
            builder.addAll(items);
        }

        itemStack.setData(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.BUNDLE_CONTENTS))
            return;

        BundleContents bundleContents = itemStack.getData(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContents.contents().isEmpty())
            return;
            
        stringBuilder.append("bundle-contents:\n");

        List<ItemStack> contents = bundleContents.contents();
        for (int i = 0; i < contents.size(); i++) {
            stringBuilder.append("  ").append(i).append(":\n");
            String itemConfig = ItemLootItem.toComponentsSection(contents.get(i));
            stringBuilder.append(itemConfig.indent(4));
        }
    }

} 
