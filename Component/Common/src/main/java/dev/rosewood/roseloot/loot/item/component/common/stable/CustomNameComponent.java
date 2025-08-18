package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.ComponentUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class CustomNameComponent implements LootItemComponent {

    private final StringProvider value;

    public CustomNameComponent(ConfigurationSection section) {
        this.value = StringProvider.fromSection(section, "custom-name", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        String value = this.value.get(context);
        if (value != null)
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, ComponentUtil.colorifyAndComponentify(value));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.CUSTOM_NAME))
            return;

        stringBuilder.append("custom-name: '").append(ComponentUtil.decomponentifyAndDecolorify(itemStack.getData(DataComponentTypes.CUSTOM_NAME)).replace("'", "''")).append("'\n");
    }

}
