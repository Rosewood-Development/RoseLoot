package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class CanBreakComponent extends ItemAdventureComponent {

    public CanBreakComponent(ConfigurationSection section) {
        super(DataComponentTypes.CAN_BREAK, "can-break", section);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        ItemAdventureComponent.applyProperties(DataComponentTypes.CAN_BREAK, "can-break", itemStack, stringBuilder);
    }

}
