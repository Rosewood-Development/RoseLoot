package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class BannerPatternsComponent implements LootItemComponent {

    private final List<PatternData> patterns;

    public BannerPatternsComponent(ConfigurationSection section) {
        ConfigurationSection bannerPatternsSection = section.getConfigurationSection("banner-patterns");
        if (bannerPatternsSection != null) {
            this.patterns = new ArrayList<>();
            for (String key : bannerPatternsSection.getKeys(false)) {
                ConfigurationSection patternSection = bannerPatternsSection.getConfigurationSection(key);
                if (patternSection == null)
                    continue;

                StringProvider pattern = StringProvider.fromSection(patternSection, "pattern", null);
                StringProvider color = StringProvider.fromSection(patternSection, "color", "WHITE");

                if (pattern != null)
                    this.patterns.add(new PatternData(pattern, color));
            }
        } else {
            this.patterns = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        BannerPatternLayers.Builder builder = BannerPatternLayers.bannerPatternLayers();

        if (this.patterns != null)
            builder.addAll(this.patterns.stream().map(x -> x.toPatternType(context)).filter(Objects::nonNull).toList());

        itemStack.setData(DataComponentTypes.BANNER_PATTERNS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.BANNER_PATTERNS))
            return;

        BannerPatternLayers layers = itemStack.getData(DataComponentTypes.BANNER_PATTERNS);
        if (!layers.patterns().isEmpty()) {
            stringBuilder.append("banner-patterns:\n");
            int index = 0;
            for (Pattern pattern : layers.patterns()) {
                NamespacedKey key = Registry.BANNER_PATTERN.getKey(pattern.getPattern());
                if (key != null) {
                    stringBuilder.append("  ").append(index++).append(":\n");
                    stringBuilder.append("    pattern: ").append(key.asMinimalString()).append('\n');
                    stringBuilder.append("    color: ").append(pattern.getColor().name()).append('\n');
                }
            }
        }
    }

    private record PatternData(StringProvider pattern, StringProvider color) {

        public Pattern toPatternType(LootContext context) {
            Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
            PatternType patternType = registry.get(Key.key(this.pattern.get(context).toLowerCase()));
            DyeColor color = DyeColor.valueOf(this.color.get(context).toUpperCase());
            if (patternType == null)
                return null;

            return new Pattern(color, patternType);
        }

    }

}
