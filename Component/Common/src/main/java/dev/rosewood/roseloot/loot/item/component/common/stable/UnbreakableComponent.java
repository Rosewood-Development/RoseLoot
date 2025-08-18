package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class UnbreakableComponent implements LootItemComponent {

    private final boolean value;

    public UnbreakableComponent(ConfigurationSection section) {
        this.value = section.contains("unbreakable");
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value)
            itemStack.setData(DataComponentTypes.UNBREAKABLE);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.UNBREAKABLE))
            return;

        stringBuilder.append("unbreakable: true\n");
    }

}
