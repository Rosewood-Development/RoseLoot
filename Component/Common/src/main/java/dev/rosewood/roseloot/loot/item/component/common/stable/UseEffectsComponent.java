package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseEffects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class UseEffectsComponent implements LootItemComponent {

    private final Boolean canSprint;
    private final Boolean interactVibrations;
    private final NumberProvider speedMultiplier;

    public UseEffectsComponent(ConfigurationSection section) {
        ConfigurationSection useEffectsSection = section.getConfigurationSection("use-effects");
        if (useEffectsSection != null) {
            if (useEffectsSection.isBoolean("can-sprint")) {
                this.canSprint = useEffectsSection.getBoolean("can-sprint", false);
            } else {
                this.canSprint = null;
            }

            if (useEffectsSection.isBoolean("interact-vibrations")) {
                this.interactVibrations = useEffectsSection.getBoolean("interact-vibrations", true);
            } else {
                this.interactVibrations = null;
            }

            this.speedMultiplier = NumberProvider.fromSection(useEffectsSection, "speed-multiplier", null);
        } else {
            this.canSprint = null;
            this.interactVibrations = null;
            this.speedMultiplier = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        UseEffects.Builder builder = UseEffects.useEffects();

        if (this.canSprint != null)
            builder.canSprint(this.canSprint);

        if (this.interactVibrations != null)
            builder.interactVibrations(this.interactVibrations);

        if (this.speedMultiplier != null) {
            float speed = this.speedMultiplier.getFloat(context);
            builder.speedMultiplier(speed);
        }

        itemStack.setData(DataComponentTypes.USE_EFFECTS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.USE_EFFECTS))
            return;

        UseEffects useEffects = itemStack.getData(DataComponentTypes.USE_EFFECTS);
        stringBuilder.append("use-effects:\n");
        stringBuilder.append("  can-sprint: ").append(useEffects.canSprint()).append('\n');
        stringBuilder.append("  interact-vibrations: ").append(useEffects.interactVibrations()).append('\n');
        stringBuilder.append("  speed-multiplier: ").append(useEffects.speedMultiplier()).append('\n');
    }

} 
