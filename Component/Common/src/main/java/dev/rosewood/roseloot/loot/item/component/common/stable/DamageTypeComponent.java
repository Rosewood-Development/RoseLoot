package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;

public class DamageTypeComponent implements LootItemComponent {

    private final DamageType damageType;

    public DamageTypeComponent(ConfigurationSection section) {
        String damageTypeString = section.getString("damage-type");
        if (damageTypeString != null) {
            Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
            this.damageType = registry.get(Key.key(damageTypeString));
        } else {
            this.damageType = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.damageType != null)
            itemStack.setData(DataComponentTypes.DAMAGE_TYPE, this.damageType);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.DAMAGE_TYPE))
            return;

        DamageType damageType = itemStack.getData(DataComponentTypes.DAMAGE_TYPE);
        if (damageType != null)
            stringBuilder.append("damage-type: ").append(damageType.getKey().asMinimalString()).append('\n');
    }

}
