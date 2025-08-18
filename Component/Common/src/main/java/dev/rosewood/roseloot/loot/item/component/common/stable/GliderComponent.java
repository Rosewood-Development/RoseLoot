package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class GliderComponent implements LootItemComponent {

    private final boolean value;

    public GliderComponent(ConfigurationSection section) {
        this.value = section.contains("glider");
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value)
            itemStack.setData(DataComponentTypes.GLIDER);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.GLIDER))
            return;

        stringBuilder.append("glider: true\n");
    }

} 
