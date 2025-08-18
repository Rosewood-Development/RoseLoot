package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.ComponentUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LoreComponent implements LootItemComponent {

    private final StringProvider lines;

    public LoreComponent(ConfigurationSection section) {
        this.lines = StringProvider.fromSection(section, "lore", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ItemLore.Builder builder = ItemLore.lore();

        if (this.lines != null)
            builder.lines(this.lines.getList(context).stream().map(ComponentUtil::colorifyAndComponentify).toList());

        itemStack.setData(DataComponentTypes.LORE, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.LORE))
            return;

        ItemLore itemLore = itemStack.getData(DataComponentTypes.LORE);
        List<String> lines = itemLore.lines().stream()
                .map(ComponentUtil::decomponentifyAndDecolorify)
                .map(x -> x.replace("'", "''"))
                .toList();
        if (!lines.isEmpty()) {
            stringBuilder.append("lore:\n");
            for (String line : lines)
                stringBuilder.append("  - '").append(line).append('\n');
        }
    }

}
