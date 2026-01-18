package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import io.papermc.paper.datacomponent.item.SwingAnimation.Animation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class SwingAnimationComponent implements LootItemComponent {

    private final Animation type;
    private final NumberProvider duration;

    public SwingAnimationComponent(ConfigurationSection section) {
        ConfigurationSection swingAnimationSection = section.getConfigurationSection("swing-animation");
        if (swingAnimationSection != null) {
            this.type = Animation.valueOf(swingAnimationSection.getString("type", "WHACK").toUpperCase());
            this.duration = NumberProvider.fromSection(swingAnimationSection, "duration", null);
        } else {
            this.type = null;
            this.duration = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        SwingAnimation.Builder builder = SwingAnimation.swingAnimation();

        if (this.duration != null)
            builder.type(this.type);

        if (this.duration != null)
            builder.duration(this.duration.getInteger(context));

        itemStack.setData(DataComponentTypes.SWING_ANIMATION, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.SWING_ANIMATION))
            return;

        SwingAnimation swingAnimnation = itemStack.getData(DataComponentTypes.SWING_ANIMATION);
        stringBuilder.append("swing-animation:\n");
        stringBuilder.append("  type: ").append(swingAnimnation.type().name().toLowerCase()).append('\n');
        stringBuilder.append("  duration: ").append(swingAnimnation.duration()).append('\n');
    }

} 
