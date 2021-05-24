package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

public class TropicalFishBucketItemLootMeta extends ItemLootMeta {

    private DyeColor bodyColor;
    private TropicalFish.Pattern pattern;
    private DyeColor patternColor;

    public TropicalFishBucketItemLootMeta(ConfigurationSection section) {
        super(section);

        String bodyColorString = section.getString("body-color");
        if (bodyColorString != null) {
            for (DyeColor value : DyeColor.values()) {
                if (value.name().equalsIgnoreCase(bodyColorString)) {
                    this.bodyColor = value;
                    break;
                }
            }
        }

        String patternString = section.getString("pattern");
        if (patternString != null) {
            for (TropicalFish.Pattern value : TropicalFish.Pattern.values()) {
                if (value.name().equalsIgnoreCase(patternString)) {
                    this.pattern = value;
                    break;
                }
            }
        }

        String patternColorString = section.getString("pattern-color");
        if (patternColorString != null) {
            for (DyeColor value : DyeColor.values()) {
                if (value.name().equalsIgnoreCase(patternColorString)) {
                    this.patternColor = value;
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        TropicalFishBucketMeta itemMeta = (TropicalFishBucketMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.bodyColor != null) itemMeta.setBodyColor(this.bodyColor);
        if (this.pattern != null) itemMeta.setPattern(this.pattern);
        if (this.patternColor != null) itemMeta.setPatternColor(this.patternColor);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
