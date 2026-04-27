package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.component.common.ParsingUtils;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.block.banner.PatternType;
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
            RegistryKeySet<PatternType> keySet = ParsingUtils.parseRegistryTags(this.value, RegistryKey.BANNER_PATTERN, context);
            itemStack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, keySet);
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.PROVIDES_BANNER_PATTERNS))
            return;

        RegistryKeySet<PatternType> keySet = itemStack.getData(DataComponentTypes.PROVIDES_BANNER_PATTERNS);
        if (!keySet.isEmpty()) {
            stringBuilder.append("provides-banner-patterns:\n");
            if (keySet instanceof Tag<?> tag) {
                String name = tag.tagKey().key().asMinimalString();
                stringBuilder.append("  - '#").append(name).append("'\n");
            } else {
                for (TypedKey<PatternType> typedKey : keySet.values()) {
                    String name = typedKey.key().asMinimalString();
                    stringBuilder.append("  - '").append(name).append("'\n");
                }
            }
        }
    }

}
