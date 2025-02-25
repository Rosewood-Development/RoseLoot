package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.ComponentUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class ItemNameComponent implements LootItemComponent {

    private final StringProvider value;

    public ItemNameComponent(ConfigurationSection section) {
        this.value = StringProvider.fromSection(section, "item-name", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        String value = this.value.get(context);
        if (value != null)
            itemStack.setData(DataComponentTypes.ITEM_NAME, ComponentUtil.colorifyAndComponentify(value));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.ITEM_NAME))
            return;

        stringBuilder.append("item-name: '").append(ComponentUtil.decomponentifyAndDecolorify(itemStack.getData(DataComponentTypes.ITEM_NAME)).replace("'", "''")).append("'\n");
    }

}
