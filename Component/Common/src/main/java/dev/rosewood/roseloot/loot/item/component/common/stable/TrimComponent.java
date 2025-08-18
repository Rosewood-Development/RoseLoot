package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimComponent implements LootItemComponent {

    private final ArmorTrim armorTrim;

    public TrimComponent(ConfigurationSection section) {
        ConfigurationSection armorTrimSection = section.getConfigurationSection("trim");
        if (armorTrimSection != null) {
            String materialString = armorTrimSection.getString("material");
            TrimMaterial material = null;
            if (materialString != null) {
                try {
                    material = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(Key.key(materialString));
                } catch (IllegalArgumentException ignored) { }
            }

            String patternString = armorTrimSection.getString("pattern");
            TrimPattern pattern = null;
            if (patternString != null) {
                try {
                    pattern = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(Key.key(patternString));
                } catch (IllegalArgumentException ignored) { }
            }

            this.armorTrim = material != null && pattern != null ? new ArmorTrim(material, pattern) : null;
        } else {
            this.armorTrim = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.armorTrim != null) {
            itemStack.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(this.armorTrim));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.TRIM))
            return;

        ItemArmorTrim itemArmorTrim = itemStack.getData(DataComponentTypes.TRIM);
        ArmorTrim armorTrim = itemArmorTrim.armorTrim();

        stringBuilder.append("armor-trim:\n");
        NamespacedKey materialKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(armorTrim.getMaterial());
        if (materialKey != null)
            stringBuilder.append("  material: ").append(materialKey.asMinimalString()).append("\n");
        NamespacedKey patternKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(armorTrim.getPattern());
        if (patternKey != null)
            stringBuilder.append("  pattern: ").append(patternKey.asMinimalString()).append("\n");
    }

} 
