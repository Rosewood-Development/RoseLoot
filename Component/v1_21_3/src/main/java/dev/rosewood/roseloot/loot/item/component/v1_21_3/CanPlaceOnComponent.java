package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class CanPlaceOnComponent extends ItemAdventureComponent {

    public CanPlaceOnComponent(ConfigurationSection section) {
        super(DataComponentTypes.CAN_PLACE_ON, "can-place-on", section);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        ItemAdventureComponent.applyProperties(DataComponentTypes.CAN_PLACE_ON, "can-place-on", itemStack, stringBuilder);
    }

}
