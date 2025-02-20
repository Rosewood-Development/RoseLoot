package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Unbreakable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class UnbreakableComponent implements LootItemComponent {

    private final Boolean showInTooltip;

    public UnbreakableComponent(ConfigurationSection section) {
        ConfigurationSection unbreakableSection = section.getConfigurationSection("unbreakable");
        if (unbreakableSection != null) {
            if (unbreakableSection.isBoolean("show-in-tooltip")) {
                this.showInTooltip = unbreakableSection.getBoolean("show-in-tooltip");
            } else {
                this.showInTooltip = null;
            }
        } else {
            this.showInTooltip = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        Unbreakable.Builder builder = Unbreakable.unbreakable();

        if (this.showInTooltip != null)
            builder.showInTooltip(this.showInTooltip);

        itemStack.setData(DataComponentTypes.UNBREAKABLE, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.UNBREAKABLE))
            return;

        Unbreakable unbreakable = itemStack.getData(DataComponentTypes.UNBREAKABLE);
        stringBuilder.append("unbreakable:\n");
        stringBuilder.append("  show-in-tooltip: " + unbreakable.showInTooltip()).append('\n');
    }

}
