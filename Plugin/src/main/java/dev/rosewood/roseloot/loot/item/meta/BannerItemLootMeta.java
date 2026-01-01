package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerItemLootMeta extends ItemLootMeta {

    private List<Pattern> patterns;

    public BannerItemLootMeta(ConfigurationSection section) {
        super(section);

        ConfigurationSection patternsSection = section.getConfigurationSection("patterns");
        if (patternsSection != null) {
            this.patterns = new ArrayList<>();
            for (String key : patternsSection.getKeys(false)) {
                ConfigurationSection patternSection = patternsSection.getConfigurationSection(key);
                if (patternSection == null)
                    continue;

                String colorString = patternSection.getString("color");
                String patternString = patternSection.getString("pattern");

                DyeColor dyeColor = null;
                for (DyeColor value : DyeColor.values()) {
                    if (value.name().equalsIgnoreCase(colorString)) {
                        dyeColor = value;
                        break;
                    }
                }

                PatternType pattern = VersionUtils.getPatternType(patternString);
                if (dyeColor != null && pattern != null)
                    this.patterns.add(new Pattern(dyeColor, pattern));
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        if (!(itemStack.getItemMeta() instanceof BannerMeta itemMeta))
            return itemStack;

        if (this.patterns != null && !this.patterns.isEmpty()) itemMeta.setPatterns(this.patterns);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof BannerMeta itemMeta))
            return;

        List<Pattern> patterns = itemMeta.getPatterns();
        if (!patterns.isEmpty()) {
            stringBuilder.append("patterns:\n");
            for (int i = 0; i < patterns.size(); i++) {
                Pattern pattern = patterns.get(i);
                stringBuilder.append("  ").append(i).append(":\n");
                stringBuilder.append("    color: ").append(pattern.getColor().name().toLowerCase()).append('\n');
                stringBuilder.append("    pattern: ").append(VersionUtils.getPatternTypeKey(pattern.getPattern()).toString().toLowerCase()).append('\n');
            }
        }
    }

}
