package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class TooltipStyleComponent implements LootItemComponent {

    private final StringProvider value;

    public TooltipStyleComponent(ConfigurationSection section) {
        this.value = StringProvider.fromSection(section, "tooltip-style", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        String value = this.value.get(context);
        if (value != null)
            itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, Key.key(value));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.TOOLTIP_STYLE))
            return;

        stringBuilder.append("tooltip-style: '").append(itemStack.getData(DataComponentTypes.TOOLTIP_STYLE).asMinimalString()).append("'\n");
    }

}
