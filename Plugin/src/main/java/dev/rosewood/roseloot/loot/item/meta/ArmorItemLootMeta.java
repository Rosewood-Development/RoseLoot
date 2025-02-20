package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ArmorItemLootMeta extends ItemLootMeta {

    private ArmorTrim armorTrim;

    public ArmorItemLootMeta(ConfigurationSection section) {
        super(section);

        ConfigurationSection trimSection = section.getConfigurationSection("trim");
        if (trimSection == null)
            return;

        NamespacedKey materialKey = NamespacedKey.fromString(trimSection.getString("material", ""));
        NamespacedKey patternKey = NamespacedKey.fromString(trimSection.getString("pattern", ""));

        if (materialKey == null || patternKey == null)
            return;

        TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(materialKey);
        TrimPattern trimPattern = Registry.TRIM_PATTERN.get(patternKey);

        if (trimMaterial != null && trimPattern != null)
            this.armorTrim = new ArmorTrim(trimMaterial, trimPattern);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        ArmorMeta itemMeta = (ArmorMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.armorTrim != null) itemMeta.setTrim(this.armorTrim);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        ArmorMeta itemMeta = (ArmorMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        ArmorTrim armorTrim = itemMeta.getTrim();
        if (armorTrim != null) {
            stringBuilder.append("trim:\n");
            stringBuilder.append("  material: ").append(Registry.TRIM_MATERIAL.getKeyOrThrow(armorTrim.getMaterial()).getKey().toLowerCase()).append('\n');
            stringBuilder.append("  pattern: ").append(Registry.TRIM_PATTERN.getKeyOrThrow(armorTrim.getPattern()).getKey().toLowerCase()).append('\n');
        }
    }

}
