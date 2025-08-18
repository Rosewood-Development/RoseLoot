package dev.rosewood.roseloot.loot.item.component.v1_21_4;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

class AttributeModifiersComponent implements LootItemComponent {

    private final List<AttributeModifierData> attributeModifiers;

    public AttributeModifiersComponent(ConfigurationSection section) {
        ConfigurationSection attributeModifiersSection = section.getConfigurationSection("attribute-modifiers");
        if (attributeModifiersSection != null) {
            this.attributeModifiers = new ArrayList<>();
            Registry<Attribute> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);
            for (String key : attributeModifiersSection.getKeys(false)) {
                ConfigurationSection modifierSection = attributeModifiersSection.getConfigurationSection(key);
                if (modifierSection == null)
                    continue;

                String attributeKey = modifierSection.getString("attribute");
                if (attributeKey == null || attributeKey.isEmpty())
                    continue;

                String identifier = modifierSection.getString("identifier");
                if (identifier == null)
                    identifier = "roseloot:" + UUID.randomUUID();
                NamespacedKey namespacedKey = NamespacedKey.fromString(identifier);

                Attribute attribute = registry.get(Key.key(attributeKey));
                if (attribute == null)
                    continue;

                NumberProvider amount = NumberProvider.fromSection(modifierSection, "amount", 0);

                String operationName = modifierSection.getString("operation");
                if (operationName == null)
                    continue;

                AttributeModifier.Operation operation = null;
                for (AttributeModifier.Operation value : AttributeModifier.Operation.values()) {
                    if (value.name().equalsIgnoreCase(operationName)) {
                        operation = value;
                        break;
                    }
                }

                if (operation == null)
                    break;

                String group = modifierSection.getString("slot-group");
                EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.getByName(group);
                if (equipmentSlotGroup == null)
                    equipmentSlotGroup = EquipmentSlotGroup.ANY;

                this.attributeModifiers.add(new AttributeModifierData(attribute, namespacedKey, amount, operation, equipmentSlotGroup));
            }
        } else {
            this.attributeModifiers = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        if (this.attributeModifiers != null)
            for (AttributeModifierData attributeModifierData : this.attributeModifiers)
                builder.addModifier(attributeModifierData.attribute(), attributeModifierData.toAttributeModifier(context));

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.ATTRIBUTE_MODIFIERS))
            return;

        ItemAttributeModifiers itemAttributeModifiers = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        List<ItemAttributeModifiers.Entry> modifiers = itemAttributeModifiers.modifiers();
        if (!modifiers.isEmpty()) {
            stringBuilder.append("attribute-modifiers:\n");
            int i = 0;
            for (ItemAttributeModifiers.Entry entry : modifiers) {
                AttributeModifier modifier = entry.modifier();
                stringBuilder.append("  ").append(i++).append(":\n");
                stringBuilder.append("    ").append("attribute: '").append(entry.attribute().getKey().asMinimalString()).append("'\n");
                stringBuilder.append("    ").append("identifier: '").append(modifier.key().asMinimalString()).append("'\n");
                stringBuilder.append("    ").append("amount: ").append(modifier.getAmount()).append('\n');
                stringBuilder.append("    ").append("operation: ").append(modifier.getOperation().name().toLowerCase()).append('\n');
                stringBuilder.append("    ").append("slot-group: ").append(modifier.getSlotGroup()).append('\n');
            }
        }
    }

    private record AttributeModifierData(Attribute attribute,
                                         NamespacedKey key,
                                         NumberProvider amount,
                                         AttributeModifier.Operation operation,
                                         EquipmentSlotGroup equipmentSlotGroup) {

        public AttributeModifier toAttributeModifier(LootContext context) {
            return new AttributeModifier(this.key, this.amount.getDouble(context), this.operation, this.equipmentSlotGroup);
        }

    }

}
