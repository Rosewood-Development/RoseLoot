package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class ProvidesTrimMaterialComponent implements LootItemComponent {

    private final StringProvider value;

    public ProvidesTrimMaterialComponent(ConfigurationSection section) {
        this.value = StringProvider.fromSection(section, "provides-trim-material", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value != null) {
            String keyValue = this.value.get(context).toLowerCase();
            Key key = Key.key(keyValue);
            Registry<TrimMaterial> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
            TrimMaterial trimMaterial = registry.get(key);
            if (trimMaterial != null)
                itemStack.setData(DataComponentTypes.PROVIDES_TRIM_MATERIAL, trimMaterial);
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.PROVIDES_TRIM_MATERIAL))
            return;

        Registry<TrimMaterial> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
        Key key = registry.getKey(itemStack.getData(DataComponentTypes.PROVIDES_TRIM_MATERIAL));
        if (key != null)
            stringBuilder.append("provides-trim-material: '").append(key.asMinimalString()).append("'\n");
    }

}
