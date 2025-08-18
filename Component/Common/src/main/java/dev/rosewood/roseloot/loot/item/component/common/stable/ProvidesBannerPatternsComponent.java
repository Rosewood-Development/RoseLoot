package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ProvidesBannerPatternsComponent implements LootItemComponent {

    private final StringProvider value;

    public ProvidesBannerPatternsComponent(ConfigurationSection section) {
        this.value = StringProvider.fromSection(section, "provides-banner-patterns", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value != null) {
            String keyValue = this.value.get(context).toLowerCase();
            if (keyValue.startsWith("#"))
                keyValue = keyValue.substring(1);
            Key key = Key.key(keyValue);
            itemStack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, TagKey.create(RegistryKey.BANNER_PATTERN, key));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.PROVIDES_BANNER_PATTERNS))
            return;

        stringBuilder.append("provides-banner-patterns: '#").append(itemStack.getData(DataComponentTypes.PROVIDES_BANNER_PATTERNS).key().asMinimalString()).append("'\n");
    }

}
