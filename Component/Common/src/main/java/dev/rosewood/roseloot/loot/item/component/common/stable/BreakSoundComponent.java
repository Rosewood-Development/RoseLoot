package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class BreakSoundComponent implements LootItemComponent {

    private final StringProvider key;

    public BreakSoundComponent(ConfigurationSection section) {
        this.key = StringProvider.fromSection(section, "break-sound", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.key != null)
            itemStack.setData(DataComponentTypes.BREAK_SOUND, Key.key(this.key.get(context).toLowerCase()));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.BREAK_SOUND))
            return;

        stringBuilder.append("break-sound: ").append(itemStack.getData(DataComponentTypes.BREAK_SOUND).asMinimalString()).append('\n');
    }

}
