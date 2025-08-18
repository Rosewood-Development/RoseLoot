package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class DamageComponent implements LootItemComponent {

    private final NumberProvider value;

    public DamageComponent(ConfigurationSection section) {
        this.value = NumberProvider.fromSection(section, "damage", -1);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        int value = this.value.getInteger(context);
        if (value >= 0)
            itemStack.setData(DataComponentTypes.DAMAGE, value);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.DAMAGE))
            return;

        stringBuilder.append("damage: ").append(itemStack.getData(DataComponentTypes.DAMAGE)).append('\n');
    }

}
