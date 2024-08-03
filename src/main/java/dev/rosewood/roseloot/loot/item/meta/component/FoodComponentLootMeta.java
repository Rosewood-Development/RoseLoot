package dev.rosewood.roseloot.loot.item.meta.component;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.ItemGenerativeLootItem;
import dev.rosewood.roseloot.loot.item.ItemLootItem;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.meta.PotionItemLootMeta;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodComponentLootMeta implements ComponentLootMeta {

    private final NumberProvider nutrition;
    private final NumberProvider saturation;
    private final NumberProvider eatSeconds;
    private Boolean canAlwaysEat;
    private ItemGenerativeLootItem convertToItem;
    private List<FoodEffectData> effects;

    public FoodComponentLootMeta(ConfigurationSection section) {
        this.nutrition = NumberProvider.fromSection(section, "nutrition", null);
        this.saturation = NumberProvider.fromSection(section, "saturation", null);
        this.eatSeconds = NumberProvider.fromSection(section, "eat-seconds", null);
        if (section.isBoolean("can-always-eat")) this.canAlwaysEat = section.getBoolean("can-always-eat");

        ConfigurationSection convertToItemSection = section.getConfigurationSection("convert-to-item");
        if (convertToItemSection != null) {
            LootItem lootItem = RoseLoot.getInstance().getManager(LootTableManager.class).parseLootItem("$internal", "none", "none", "food-component", convertToItemSection);
            if (lootItem instanceof ItemGenerativeLootItem itemGenerativeLootItem) {
                this.convertToItem = itemGenerativeLootItem;
            } else {
                RoseLoot.getInstance().getLogger().warning("Ignoring food-component convert-to-item because it does not generate an ItemStack");
            }
        }

        ConfigurationSection effectsSection = section.getConfigurationSection("effects");
        if (effectsSection != null) {
            this.effects = new ArrayList<>();
            for (String key : effectsSection.getKeys(false)) {
                ConfigurationSection effectSection = effectsSection.getConfigurationSection(key);
                if (effectSection != null) {
                    NumberProvider probability = NumberProvider.fromSection(effectSection, "probability", 1.0);
                    String effectString = effectSection.getString("effect");
                    if (effectString == null)
                        continue;

                    PotionEffectType effect = PotionEffectType.getByName(effectString);
                    if (effect == null)
                        continue;

                    NumberProvider duration = NumberProvider.fromSection(effectSection, "duration", 200);
                    NumberProvider amplifier = NumberProvider.fromSection(effectSection, "amplifier", 0);
                    boolean ambient = effectSection.getBoolean("ambient", false);
                    boolean particles = effectSection.getBoolean("particles", true);
                    boolean icon = effectSection.getBoolean("icon", true);
                    boolean overwrite = effectSection.getBoolean("overwrite", true);

                    this.effects.add(new FoodEffectData(new PotionItemLootMeta.PotionEffectData(effect, duration, amplifier, ambient, particles, icon), probability));
                }
            }
        }
    }

    @Override
    public void apply(ItemMeta itemMeta, LootContext context) {
        FoodComponent foodComponent = itemMeta.getFood();

        if (this.nutrition != null) foodComponent.setNutrition(this.nutrition.getInteger(context));
        if (this.saturation != null) foodComponent.setSaturation((float) this.saturation.getDouble(context));
        if (this.eatSeconds != null) foodComponent.setEatSeconds((float) this.eatSeconds.getDouble(context));
        if (this.canAlwaysEat != null) foodComponent.setCanAlwaysEat(this.canAlwaysEat);

        if (this.convertToItem != null) {
            List<ItemStack> generatedItems = this.convertToItem.generate(context);
            if (generatedItems.size() == 1) {
                foodComponent.setUsingConvertsTo(generatedItems.get(0));
            } else {
                RoseLoot.getInstance().getLogger().warning("Skipped adding food-component convert-to-item because it generated either more than one item or none at all");
            }
        }

        if (this.effects != null) {
            for (FoodEffectData effectData : this.effects) {
                PotionEffect potionEffect = effectData.potionEffectData().toPotionEffect(context);
                float probability = (float) effectData.probability().getDouble(context);
                foodComponent.addEffect(potionEffect, probability);
            }
        }

        itemMeta.setFood(foodComponent);
    }

    public static void applyProperties(ItemMeta itemMeta, StringBuilder stringBuilder) {
        if (!itemMeta.hasFood())
            return;

        FoodComponent foodComponent = itemMeta.getFood();

        stringBuilder.append("food-component:\n");
        stringBuilder.append("  nutrition: ").append(foodComponent.getNutrition()).append('\n');
        stringBuilder.append("  saturation: ").append(foodComponent.getSaturation()).append('\n');
        stringBuilder.append("  can-always-eat: ").append(foodComponent.canAlwaysEat()).append('\n');
        stringBuilder.append("  eat-seconds: ").append(foodComponent.getEatSeconds()).append('\n');

        if (foodComponent.getUsingConvertsTo() != null) {
            stringBuilder.append("  convert-to-item:\n");
            stringBuilder.append("    type: item\n");
            ItemStack convertToItem = foodComponent.getUsingConvertsTo();
            String convertToItemProperties = ItemLootItem.toSection(convertToItem, false).lines().map(x -> "    " + x).collect(Collectors.joining("\n"));
            stringBuilder.append(convertToItemProperties).append('\n');
        }

        List<FoodComponent.FoodEffect> foodEffects = foodComponent.getEffects();
        if (!foodEffects.isEmpty()) {
            stringBuilder.append("  effects:\n");
            for (int i = 0; i < foodEffects.size(); i++) {
                FoodComponent.FoodEffect foodEffect = foodEffects.get(i);
                PotionEffect effect = foodEffect.getEffect();
                stringBuilder.append("  ").append(i).append(":\n");
                stringBuilder.append("    probability: ").append(foodEffect.getProbability()).append('\n');
                stringBuilder.append("    effect: ").append(effect.getType().getName().toLowerCase()).append('\n');
                stringBuilder.append("    duration: ").append(effect.getDuration()).append('\n');
                stringBuilder.append("    amplifier: ").append(effect.getAmplifier()).append('\n');
                stringBuilder.append("    ambient: ").append(effect.isAmbient()).append('\n');
                stringBuilder.append("    particles: ").append(effect.hasParticles()).append('\n');
                stringBuilder.append("    icon: ").append(effect.hasIcon()).append('\n');
            }
        }
    }

    private record FoodEffectData(PotionItemLootMeta.PotionEffectData potionEffectData, NumberProvider probability) { }

}
