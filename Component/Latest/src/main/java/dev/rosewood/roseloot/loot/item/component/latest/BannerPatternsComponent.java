package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class BannerPatternsComponent implements LootItemComponent {

    private final List<Pattern> patterns;

    public BannerPatternsComponent(ConfigurationSection section) {
        ConfigurationSection patternsSection = section.getConfigurationSection("banner-patterns");
        if (patternsSection != null) {
            this.patterns = new ArrayList<>();
            Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
            for (String key : patternsSection.getKeys(false)) {
                ConfigurationSection patternSection = patternsSection.getConfigurationSection(key);
                if (patternSection == null)
                    continue;

                String patternKey = patternSection.getString("pattern");
                String colorName = patternSection.getString("color", "WHITE");

                if (patternKey != null) {
                    try {
                        PatternType patternType = registry.get(Key.key(patternKey.toLowerCase()));
                        DyeColor color = DyeColor.valueOf(colorName.toUpperCase());
                        if (patternType != null) {
                            this.patterns.add(new Pattern(color, patternType));
                        }
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } else {
            this.patterns = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        BannerPatternLayers.Builder builder = BannerPatternLayers.bannerPatternLayers();

        if (this.patterns != null)
            builder.addAll(this.patterns);

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

} 
