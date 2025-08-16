package dev.rosewood.roseloot.loot.item.component.v1_21_3;

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
        return Map.<String, Function<ConfigurationSection, LootItemComponent>>ofEntries(
                Map.entry("attribute-modifiers", AttributeModifiersComponent::new),
                Map.entry("banner-patterns", BannerPatternsComponent::new),
                Map.entry("base-color", BaseColorComponent::new),
                Map.entry("bundle-contents", BundleContentsComponent::new),
                Map.entry("can-break", CanBreakComponent::new),
                Map.entry("can-place-on", CanPlaceOnComponent::new),
                Map.entry("charged-projectiles", ChargedProjectilesComponent::new),
                Map.entry("consumable", ConsumableComponent::new),
                Map.entry("container", ContainerComponent::new),
                Map.entry("container-loot", ContainerLootComponent::new),
                Map.entry("custom-model-data", CustomModelDataComponent::new),
                Map.entry("custom-name", CustomNameComponent::new),
                Map.entry("damage", DamageComponent::new),
                Map.entry("damage-resistant", DamageResistantComponent::new),
                Map.entry("death-protection", DeathProtectionComponent::new),
                Map.entry("dyed-color", DyedColorComponent::new),
                Map.entry("enchantable", EnchantableComponent::new),
                Map.entry("enchantment-glint-override", EnchantmentGlintOverrideComponent::new),
                Map.entry("enchantments", EnchantmentsComponent::new),
                Map.entry("equippable", EquippableComponent::new),
                Map.entry("stored-enchantments", StoredEnchantmentsComponent::new),
                Map.entry("firework-explosion", FireworkExplosionComponent::new),
                Map.entry("fireworks", FireworksComponent::new),
                Map.entry("food", FoodComponent::new),
                Map.entry("glider", GliderComponent::new),
                Map.entry("hide-additional-tooltip", HideAdditionalTooltipComponent::new),
                Map.entry("hide-tooltip", HideTooltipComponent::new),
                Map.entry("intangible-projectile", IntangibleProjectileComponent::new),
                Map.entry("item-model", ItemModelComponent::new),
                Map.entry("item-name", ItemNameComponent::new),
                Map.entry("jukebox-playable", JukeboxPlayableComponent::new),
                Map.entry("lore", LoreComponent::new),
                Map.entry("map-color", MapColorComponent::new),
                Map.entry("map-decorations", MapDecorationsComponent::new),
                Map.entry("map-id", MapIdComponent::new),
                Map.entry("map-post-processing", MapPostProcessingComponent::new),
                Map.entry("max-damage", MaxDamageComponent::new),
                Map.entry("max-stack-size", MaxStackSizeComponent::new),
                Map.entry("note-block-sound", NoteBlockSoundComponent::new),
                Map.entry("ominous-bottle-amplifier", OminousBottleAmplifierComponent::new),
                Map.entry("lodestone-tracker", LodestoneTrackerComponent::new),
                Map.entry("potion-contents", PotionContentsComponent::new),
                Map.entry("pot-decorations", PotDecorationsComponent::new),
                Map.entry("profile", ProfileComponent::new),
                Map.entry("rarity", RarityComponent::new),
                Map.entry("recipes", RecipesComponent::new),
                Map.entry("repair-cost", RepairCostComponent::new),
                Map.entry("repairable", RepairableComponent::new),
                Map.entry("suspicious-stew-effects", SuspiciousStewEffectsComponent::new),
                Map.entry("tool", ToolComponent::new),
                Map.entry("tooltip-style", TooltipStyleComponent::new),
                Map.entry("trim", TrimComponent::new),
                Map.entry("unbreakable", UnbreakableComponent::new),
                Map.entry("use-cooldown", UseCooldownComponent::new),
                Map.entry("use-remainder", UseRemainderComponent::new),
                Map.entry("writable-book-content", WritableBookContentComponent::new),
                Map.entry("written-book-content", WrittenBookContentComponent::new)
        );
    }

    @Override
    public Map<String, BiConsumer<ItemStack, StringBuilder>> provideLootItemComponentPropertyApplicators() {
        return Map.<String, BiConsumer<ItemStack, StringBuilder>>ofEntries(
                Map.entry("attribute-modifiers", AttributeModifiersComponent::applyProperties),
                Map.entry("banner-patterns", BannerPatternsComponent::applyProperties),
                Map.entry("base-color", BaseColorComponent::applyProperties),
                Map.entry("bundle-contents", BundleContentsComponent::applyProperties),
                Map.entry("can-break", CanBreakComponent::applyProperties),
                Map.entry("can-place-on", CanPlaceOnComponent::applyProperties),
                Map.entry("charged-projectiles", ChargedProjectilesComponent::applyProperties),
                Map.entry("consumable", ConsumableComponent::applyProperties),
                Map.entry("container", ContainerComponent::applyProperties),
                Map.entry("container-loot", ContainerLootComponent::applyProperties),
                Map.entry("custom-model-data", CustomModelDataComponent::applyProperties),
                Map.entry("custom-name", CustomNameComponent::applyProperties),
                Map.entry("damage", DamageComponent::applyProperties),
                Map.entry("damage-resistant", DamageResistantComponent::applyProperties),
                Map.entry("death-protection", DeathProtectionComponent::applyProperties),
                Map.entry("dyed-color", DyedColorComponent::applyProperties),
                Map.entry("enchantable", EnchantableComponent::applyProperties),
                Map.entry("enchantment-glint-override", EnchantmentGlintOverrideComponent::applyProperties),
                Map.entry("enchantments", EnchantmentsComponent::applyProperties),
                Map.entry("equippable", EquippableComponent::applyProperties),
                Map.entry("stored-enchantments", StoredEnchantmentsComponent::applyProperties),
                Map.entry("firework-explosion", FireworkExplosionComponent::applyProperties),
                Map.entry("fireworks", FireworksComponent::applyProperties),
                Map.entry("food", FoodComponent::applyProperties),
                Map.entry("glider", GliderComponent::applyProperties),
                Map.entry("hide-additional-tooltip", HideAdditionalTooltipComponent::applyProperties),
                Map.entry("hide-tooltip", HideTooltipComponent::applyProperties),
                Map.entry("intangible-projectile", IntangibleProjectileComponent::applyProperties),
                Map.entry("item-model", ItemModelComponent::applyProperties),
                Map.entry("item-name", ItemNameComponent::applyProperties),
                Map.entry("jukebox-playable", JukeboxPlayableComponent::applyProperties),
                Map.entry("lore", LoreComponent::applyProperties),
                Map.entry("map-color", MapColorComponent::applyProperties),
                Map.entry("map-decorations", MapDecorationsComponent::applyProperties),
                Map.entry("map-id", MapIdComponent::applyProperties),
                Map.entry("map-post-processing", MapPostProcessingComponent::applyProperties),
                Map.entry("max-damage", MaxDamageComponent::applyProperties),
                Map.entry("max-stack-size", MaxStackSizeComponent::applyProperties),
                Map.entry("note-block-sound", NoteBlockSoundComponent::applyProperties),
                Map.entry("ominous-bottle-amplifier", OminousBottleAmplifierComponent::applyProperties),
                Map.entry("lodestone-tracker", LodestoneTrackerComponent::applyProperties),
                Map.entry("potion-contents", PotionContentsComponent::applyProperties),
                Map.entry("pot-decorations", PotDecorationsComponent::applyProperties),
                Map.entry("profile", ProfileComponent::applyProperties),
                Map.entry("rarity", RarityComponent::applyProperties),
                Map.entry("recipes", RecipesComponent::applyProperties),
                Map.entry("repair-cost", RepairCostComponent::applyProperties),
                Map.entry("repairable", RepairableComponent::applyProperties),
                Map.entry("suspicious-stew-effects", SuspiciousStewEffectsComponent::applyProperties),
                Map.entry("tool", ToolComponent::applyProperties),
                Map.entry("tooltip-style", TooltipStyleComponent::applyProperties),
                Map.entry("trim", TrimComponent::applyProperties),
                Map.entry("unbreakable", UnbreakableComponent::applyProperties),
                Map.entry("use-cooldown", UseCooldownComponent::applyProperties),
                Map.entry("use-remainder", UseRemainderComponent::applyProperties),
                Map.entry("writable-book-content", WritableBookContentComponent::applyProperties),
                Map.entry("written-book-content", WrittenBookContentComponent::applyProperties)
        );
    }

}
