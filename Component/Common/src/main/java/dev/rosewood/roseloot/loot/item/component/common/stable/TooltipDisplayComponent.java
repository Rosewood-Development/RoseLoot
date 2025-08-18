package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class TooltipDisplayComponent implements LootItemComponent {

    private final Boolean hideTooltip;
    private final StringProvider hiddenComponents;

    public TooltipDisplayComponent(ConfigurationSection section) {
        ConfigurationSection tooltipDisplaySection = section.getConfigurationSection("tooltip-display");
        if (tooltipDisplaySection != null) {
            if (tooltipDisplaySection.isBoolean("hide-tooltip")) {
                this.hideTooltip = tooltipDisplaySection.getBoolean("hide-tooltip");
            } else {
                this.hideTooltip = null;
            }

            this.hiddenComponents = StringProvider.fromSection(section, "hidden-components", null);
        } else {
            this.hideTooltip = null;
            this.hiddenComponents = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        TooltipDisplay.Builder tooltipDisplay = TooltipDisplay.tooltipDisplay();

        if (this.hideTooltip != null)
            tooltipDisplay.hideTooltip(this.hideTooltip);

        if (this.hiddenComponents != null) {
            List<Key> hiddenComponents = this.hiddenComponents.getList(context).stream().map(x -> Key.key(x.toLowerCase())).toList();
            Registry<DataComponentType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE);
            for (Key key : hiddenComponents) {
                DataComponentType componentType = registry.get(key);
                if (componentType != null)
                    tooltipDisplay.addHiddenComponents(componentType);
            }
        }

        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.TOOLTIP_DISPLAY))
            return;

        TooltipDisplay tooltipDisplay = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);

        stringBuilder.append("tooltip-display:\n");
        stringBuilder.append("  hide-tooltip: ").append(tooltipDisplay.hideTooltip()).append('\n');

        Set<DataComponentType> hiddenComponents = tooltipDisplay.hiddenComponents();
        if (!hiddenComponents.isEmpty()) {
            stringBuilder.append("  hidden-components:\n");
            Registry<DataComponentType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE);
            for (DataComponentType componentType : hiddenComponents) {
                Key key = registry.getKey(componentType);
                if (key != null)
                    stringBuilder.append("    - '").append(key.asMinimalString()).append("'\n");
            }
        }
    }

}
