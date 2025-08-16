package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.manager.LootTableManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class ContainerComponent implements LootItemComponent {

    private final List<LootItem> contents;

    public ContainerComponent(ConfigurationSection section) {
        ConfigurationSection containerSection = section.getConfigurationSection("container");
        if (containerSection != null) {
            this.contents = new ArrayList<>();
            for (String key : containerSection.getKeys(false)) {
                ConfigurationSection contentSection = containerSection.getConfigurationSection(key);
                if (contentSection != null) {
                    LootItem lootItem = RoseLoot.getInstance().getManager(LootTableManager.class).parseLootItem("$internal", "none", "container", key, contentSection);
                    if (lootItem != null) {
                        this.contents.add(lootItem);
                    } else {
                        RoseLoot.getInstance().getLogger().warning("Ignoring invalid container item: " + key);
                    }
                }
            }
        } else {
            this.contents = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ItemContainerContents.Builder builder = ItemContainerContents.containerContents();

        if (this.contents != null) {
            LootContents lootContents = new LootContents(context);
            lootContents.add(this.contents);
            builder.addAll(lootContents.getItems());
        }

        itemStack.setData(DataComponentTypes.CONTAINER, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.CONTAINER))
            return;

        ItemContainerContents container = itemStack.getData(DataComponentTypes.CONTAINER);
        if (!container.contents().isEmpty()) {
            stringBuilder.append("container:\n");
            int index = 0;
            for (ItemStack item : container.contents()) {
                stringBuilder.append("  ").append(index++).append(":\n");
                stringBuilder.append("    item: ").append(item).append('\n');
            }
        }
    }

}
