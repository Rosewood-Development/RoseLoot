package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class OminousBottleAmplifierComponent implements LootItemComponent {

    private final NumberProvider value;

    public OminousBottleAmplifierComponent(ConfigurationSection section) {
        ConfigurationSection amplifierSection = section.getConfigurationSection("ominous-bottle-amplifier");
        if (amplifierSection != null) {
            this.value = NumberProvider.fromSection(amplifierSection, "value", null);
        } else {
            this.value = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value != null) {
            int amplifierValue = Math.min(4, Math.max(0, this.value.getInteger(context)));
            itemStack.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, OminousBottleAmplifier.amplifier(amplifierValue));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER))
            return;

        OminousBottleAmplifier amplifier = itemStack.getData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
        stringBuilder.append("ominous-bottle-amplifier: ").append(amplifier.amplifier()).append('\n');
    }
} 
