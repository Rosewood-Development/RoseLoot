package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

class TrimComponent implements LootItemComponent {

    private final ArmorTrim armorTrim;
    private final boolean showInTooltip;

    public TrimComponent(ConfigurationSection section) {
        ConfigurationSection armorTrimSection = section.getConfigurationSection("trim");
        if (armorTrimSection != null) {
            // Parse material
            String materialString = armorTrimSection.getString("material");
            TrimMaterial material = null;
            if (materialString != null) {
                try {
                    material = Registry.TRIM_MATERIAL.get(Key.key(materialString));
                } catch (IllegalArgumentException ignored) { }
            }

            // Parse pattern
            String patternString = armorTrimSection.getString("pattern");
            TrimPattern pattern = null;
            if (patternString != null) {
                try {
                    pattern = Registry.TRIM_PATTERN.get(Key.key(patternString));
                } catch (IllegalArgumentException ignored) { }
            }

            // Create armor trim if both material and pattern are valid
            this.armorTrim = material != null && pattern != null ? new ArmorTrim(material, pattern) : null;

            // Parse show in tooltip
            this.showInTooltip = armorTrimSection.getBoolean("show-in-tooltip", true);
        } else {
            this.armorTrim = null;
            this.showInTooltip = true;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.armorTrim != null) {
            itemStack.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(this.armorTrim, this.showInTooltip));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.TRIM))
            return;

        ItemArmorTrim itemArmorTrim = itemStack.getData(DataComponentTypes.TRIM);
        ArmorTrim armorTrim = itemArmorTrim.armorTrim();
        
        stringBuilder.append("armor-trim:\n");
        stringBuilder.append("  material: ").append(armorTrim.getMaterial().getKey().getKey()).append("\n");
        stringBuilder.append("  pattern: ").append(armorTrim.getPattern().getKey().getKey()).append("\n");
        stringBuilder.append("  show-in-tooltip: ").append(itemArmorTrim.showInTooltip()).append("\n");
    }

} 
