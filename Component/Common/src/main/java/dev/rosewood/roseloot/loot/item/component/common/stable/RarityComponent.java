package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

public class RarityComponent implements LootItemComponent {

    private final ItemRarity rarity;

    public RarityComponent(ConfigurationSection section) {
        ItemRarity rarity = null;
        String rarityString = section.getString("rarity");
        if (rarityString != null) {
            try {
                rarity = ItemRarity.valueOf(rarityString.toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }
        this.rarity = rarity;
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.rarity != null)
            itemStack.setData(DataComponentTypes.RARITY, this.rarity);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.RARITY))
            return;

        ItemRarity rarity = itemStack.getData(DataComponentTypes.RARITY);
        stringBuilder.append("rarity: ").append(rarity.name().toLowerCase()).append('\n');
    }

}
