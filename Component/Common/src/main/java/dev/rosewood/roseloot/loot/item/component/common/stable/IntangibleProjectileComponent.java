package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class IntangibleProjectileComponent implements LootItemComponent {

    private final boolean value;

    public IntangibleProjectileComponent(ConfigurationSection section) {
        this.value = section.contains("intangible-projectile");
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value)
            itemStack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.INTANGIBLE_PROJECTILE))
            return;

        stringBuilder.append("intangible-projectile: true\n");
    }

}
