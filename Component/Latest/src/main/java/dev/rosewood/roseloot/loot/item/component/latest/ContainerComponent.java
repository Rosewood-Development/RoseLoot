package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ItemGenerativeLootItem;
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

    private final List<ItemGenerativeLootItem> contents;

    public ContainerComponent(ConfigurationSection section) {
        ConfigurationSection containerSection = section.getConfigurationSection("container");
        if (containerSection != null) {
            this.contents = new ArrayList<>();
            for (String key : containerSection.getKeys(false)) {
                ConfigurationSection contentSection = containerSection.getConfigurationSection(key);
                if (contentSection != null) {
                    LootItem lootItem = RoseLoot.getInstance().getManager(LootTableManager.class).parseLootItem("$internal", "none", "none", "container", contentSection);
                    if (lootItem instanceof ItemGenerativeLootItem itemGenerativeLootItem) {
                        this.contents.add(itemGenerativeLootItem);
                    } else {
                        RoseLoot.getInstance().getLogger().warning("Ignoring container entry because it does not generate an ItemStack");
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

        if (this.contents != null)
            for (ItemGenerativeLootItem content : this.contents)
                builder.addAll(content.generate(context));

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
