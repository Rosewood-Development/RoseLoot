package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ItemGenerativeLootItem;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.manager.LootTableManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class UseRemainderComponent implements LootItemComponent {

    private final ItemGenerativeLootItem transformInto;

    public UseRemainderComponent(ConfigurationSection section) {
        ConfigurationSection useRemainderSection = section.getConfigurationSection("use-remainder");
        if (useRemainderSection != null) {
            ConfigurationSection transformIntoSection = useRemainderSection.getConfigurationSection("transform-into");
            if (transformIntoSection != null) {
                LootItem lootItem = RoseLoot.getInstance().getManager(LootTableManager.class).parseLootItem("$internal", "none", "none", "use-remainder", transformIntoSection);
                if (lootItem instanceof ItemGenerativeLootItem itemGenerativeLootItem) {
                    this.transformInto = itemGenerativeLootItem;
                } else {
                    RoseLoot.getInstance().getLogger().warning("Ignoring use-remainder transform-into because it does not generate an ItemStack");
                    this.transformInto = null;
                }
            } else {
                this.transformInto = null;
            }
        } else {
            this.transformInto = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.transformInto != null) {
            List<ItemStack> transformedItems = this.transformInto.generate(context);
            if (!transformedItems.isEmpty())
                itemStack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(transformedItems.getFirst()));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.USE_REMAINDER))
            return;

        UseRemainder useRemainder = itemStack.getData(DataComponentTypes.USE_REMAINDER);
        stringBuilder.append("use-remainder:\n");
        stringBuilder.append("  transform-into:\n");

        ItemStack transformIntoItem = useRemainder.transformInto();
        String itemConfig = ItemLootItem.toComponentsSection(transformIntoItem);
        stringBuilder.append(itemConfig.indent(4));
    }

} 
