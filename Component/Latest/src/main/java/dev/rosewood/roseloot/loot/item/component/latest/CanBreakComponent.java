package dev.rosewood.roseloot.loot.item.component.latest;

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
