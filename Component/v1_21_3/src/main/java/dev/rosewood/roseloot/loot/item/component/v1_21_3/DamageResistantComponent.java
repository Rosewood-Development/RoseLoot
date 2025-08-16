package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;

class DamageResistantComponent implements LootItemComponent {

    private final StringProvider types;

    public DamageResistantComponent(ConfigurationSection section) {
        ConfigurationSection damageResistantSection = section.getConfigurationSection("damage-resistant");
        if (damageResistantSection != null) {
            this.types = StringProvider.fromSection(damageResistantSection, "types", null);
        } else {
            this.types = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.types != null) {
            TagKey<DamageType> tagKey = TagKey.create(RegistryKey.DAMAGE_TYPE, Key.key(this.types.get(context).toLowerCase()));
            itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(tagKey));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.DAMAGE_RESISTANT))
            return;

        DamageResistant damageResistant = itemStack.getData(DataComponentTypes.DAMAGE_RESISTANT);
        stringBuilder.append("damage-resistant:\n");
        stringBuilder.append("  types: '").append(damageResistant.types().key().asMinimalString()).append("'\n");
    }

} 
