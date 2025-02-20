package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MaxDamageComponent implements LootItemComponent {

    private final NumberProvider value;

    public MaxDamageComponent(ConfigurationSection section) {
        this.value = NumberProvider.fromSection(section, "max-damage", -1);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        int value = this.value.getInteger(context);
        if (value > 0)
            itemStack.setData(DataComponentTypes.MAX_DAMAGE, value);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.MAX_DAMAGE))
            return;

        stringBuilder.append("max-damage: ").append(itemStack.getData(DataComponentTypes.MAX_DAMAGE)).append('\n');
    }

}
