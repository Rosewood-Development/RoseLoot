package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PotionDurationScaleComponent implements LootItemComponent {

    private final NumberProvider value;

    public PotionDurationScaleComponent(ConfigurationSection section) {
        this.value = NumberProvider.fromSection(section, "potion-duration-scale", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value != null)
            itemStack.setData(DataComponentTypes.POTION_DURATION_SCALE, this.value.getFloat(context));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.POTION_DURATION_SCALE))
            return;

        stringBuilder.append("potion-duration-scale: ").append(itemStack.getData(DataComponentTypes.POTION_DURATION_SCALE)).append('\n');
    }

}
