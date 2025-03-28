package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class FoodComponent implements LootItemComponent {

    private final NumberProvider nutrition;
    private final NumberProvider saturation;
    private final boolean canAlwaysEat;

    public FoodComponent(ConfigurationSection section) {
        ConfigurationSection foodSection = section.getConfigurationSection("food");
        if (foodSection != null) {
            this.nutrition = NumberProvider.fromSection(foodSection, "nutrition", null);
            this.saturation = NumberProvider.fromSection(foodSection, "saturation", null);
            this.canAlwaysEat = foodSection.getBoolean("can-always-eat", false);
        } else {
            this.nutrition = null;
            this.saturation = null;
            this.canAlwaysEat = false;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.nutrition != null || this.saturation != null || this.canAlwaysEat) {
            FoodProperties.Builder builder = FoodProperties.food();
            
            if (this.nutrition != null)
                builder.nutrition(this.nutrition.getInteger(context));
            
            if (this.saturation != null)
                builder.saturation((float) this.saturation.getDouble(context));
            
            if (this.canAlwaysEat)
                builder.canAlwaysEat(true);
            
            itemStack.setData(DataComponentTypes.FOOD, builder.build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.FOOD))
            return;

        FoodProperties food = itemStack.getData(DataComponentTypes.FOOD);
        stringBuilder.append("food:\n");
        stringBuilder.append("  nutrition: ").append(food.nutrition()).append('\n');
        stringBuilder.append("  saturation: ").append(food.saturation()).append('\n');
        stringBuilder.append("  can-always-eat: ").append(food.canAlwaysEat()).append('\n');
    }

} 
