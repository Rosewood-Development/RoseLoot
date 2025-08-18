package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemModelComponent implements LootItemComponent {

    private final StringProvider value;

    public ItemModelComponent(ConfigurationSection section) {
        this.value = StringProvider.fromSection(section, "item-model", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        String value = this.value.get(context);
        if (value != null)
            itemStack.setData(DataComponentTypes.ITEM_MODEL, Key.key(value));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.ITEM_MODEL))
            return;

        stringBuilder.append("item-model: '").append(itemStack.getData(DataComponentTypes.ITEM_MODEL).asMinimalString()).append("'\n");
    }

}
