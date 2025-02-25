package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.component.LootItemComponentProvider;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LootItemComponentProviderImpl implements LootItemComponentProvider {

    @Override
    public Map<String, Function<ConfigurationSection, LootItemComponent>> provideLootItemComponentConstructors() {
        return Map.ofEntries(
                Map.entry("attribute-modifiers", AttributeModifiersComponent::new),
                Map.entry("can-break", CanBreakComponent::new),
                Map.entry("can-place-on", CanPlaceOnComponent::new),
                Map.entry("custom-model-data", CustomModelDataComponent::new),
                Map.entry("custom-name", CustomNameComponent::new),
                Map.entry("damage", DamageComponent::new),
                Map.entry("enchantments", EnchantmentsComponent::new),
                Map.entry("hide-tooltip", HideTooltipComponent::new),
                Map.entry("item-model", ItemModelComponent::new),
                Map.entry("item-name", ItemNameComponent::new),
                Map.entry("lore", LoreComponent::new),
                Map.entry("max-damage", MaxDamageComponent::new),
                Map.entry("max-stack-size", MaxStackSizeComponent::new),
                Map.entry("rarity", RarityComponent::new),
                Map.entry("tooltip-style", TooltipStyleComponent::new),
                Map.entry("unbreakable", UnbreakableComponent::new)
        );
    }

    @Override
    public Map<String, BiConsumer<ItemStack, StringBuilder>> provideLootItemComponentPropertyApplicators() {
        return Map.ofEntries(
                Map.entry("attribute-modifiers", AttributeModifiersComponent::applyProperties),
                Map.entry("can-break", CanBreakComponent::applyProperties),
                Map.entry("can-place-on", CanPlaceOnComponent::applyProperties),
                Map.entry("custom-model-data", CustomModelDataComponent::applyProperties),
                Map.entry("custom-name", CustomNameComponent::applyProperties),
                Map.entry("damage", DamageComponent::applyProperties),
                Map.entry("enchantments", EnchantmentsComponent::applyProperties),
                Map.entry("hide-tooltip", HideTooltipComponent::applyProperties),
                Map.entry("item-model", ItemModelComponent::applyProperties),
                Map.entry("item-name", ItemNameComponent::applyProperties),
                Map.entry("lore", LoreComponent::applyProperties),
                Map.entry("max-damage", MaxDamageComponent::applyProperties),
                Map.entry("max-stack-size", MaxStackSizeComponent::applyProperties),
                Map.entry("rarity", RarityComponent::applyProperties),
                Map.entry("tooltip-style", TooltipStyleComponent::applyProperties),
                Map.entry("unbreakable", UnbreakableComponent::applyProperties)
        );
    }

}
