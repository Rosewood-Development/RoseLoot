package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class CustomModelDataComponent implements LootItemComponent {

    private final List<Float> floats;
    private final List<Boolean> flags;
    private final List<String> strings;
    private final List<Color> colors;

    public CustomModelDataComponent(ConfigurationSection section) {
        ConfigurationSection customModelDataSection = section.getConfigurationSection("custom-model-data");
        if (customModelDataSection != null) {
            this.floats = customModelDataSection.getFloatList("floats");
            this.flags = customModelDataSection.getBooleanList("flags");
            this.strings = customModelDataSection.getStringList("strings");
            this.colors = new ArrayList<>();
            for (String string : customModelDataSection.getStringList("colors")) {
                try {
                    java.awt.Color color = java.awt.Color.decode(string);
                    this.colors.add(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
                } catch (NumberFormatException ignored) { }
            }
        } else {
            this.floats = null;
            this.flags = null;
            this.strings = null;
            this.colors = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        CustomModelData.Builder builder = CustomModelData.customModelData();

        if (this.floats != null && !this.floats.isEmpty())
            builder.addFloats(this.floats);

        if (this.flags != null && !this.flags.isEmpty())
            builder.addFlags(this.flags);

        if (this.strings != null && !this.strings.isEmpty())
            builder.addStrings(this.strings);

        if (this.colors != null && !this.colors.isEmpty())
            builder.addColors(this.colors);

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.CUSTOM_MODEL_DATA))
            return;

        CustomModelData customModelData = itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);

        List<Float> floats = customModelData.floats();
        List<Boolean> flags = customModelData.flags();
        List<String> strings = customModelData.strings();
        List<Color> colors = customModelData.colors();

        if (!floats.isEmpty() || !flags.isEmpty() || !strings.isEmpty() || !colors.isEmpty()) {
            stringBuilder.append("custom-model-data:\n");

            if (!floats.isEmpty()) {
                stringBuilder.append("  floats:\n");
                for (float value : floats)
                    stringBuilder.append("    - ").append(value).append('\n');
            }

            if (!flags.isEmpty()) {
                stringBuilder.append("  flags:\n");
                for (boolean value : flags)
                    stringBuilder.append("    - ").append(value).append('\n');
            }

            if (!strings.isEmpty()) {
                stringBuilder.append("  strings:\n");
                for (String value : strings)
                    stringBuilder.append("    - '").append(value).append("'\n");
            }

            if (!colors.isEmpty()) {
                stringBuilder.append("  colors:\n");
                for (Color value : colors)
                    stringBuilder.append("    - '").append(String.format("#%02x%02x%02x", value.getRed(), value.getGreen(), value.getBlue())).append("'\n");
            }
        }
    }

}
