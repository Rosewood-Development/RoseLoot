package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class AttackRangeComponent implements LootItemComponent {

    private final NumberProvider minReach;
    private final NumberProvider maxReach;
    private final NumberProvider minCreativeReach;
    private final NumberProvider maxCreativeReach;
    private final NumberProvider hitboxMargin;
    private final NumberProvider mobFactor;

    public AttackRangeComponent(ConfigurationSection section) {
        ConfigurationSection attackRangeSection = section.getConfigurationSection("attack-range");
        if (attackRangeSection != null) {
            this.minReach = NumberProvider.fromSection(attackRangeSection, "min-reach", null);
            this.maxReach = NumberProvider.fromSection(attackRangeSection, "max-reach", null);
            this.minCreativeReach = NumberProvider.fromSection(attackRangeSection, "min-creative-reach", null);
            this.maxCreativeReach = NumberProvider.fromSection(attackRangeSection, "max-creative-reach", null);
            this.hitboxMargin = NumberProvider.fromSection(attackRangeSection, "hitbox-margin", null);
            this.mobFactor = NumberProvider.fromSection(attackRangeSection, "mob-factor", null);
        } else {
            this.minReach = null;
            this.maxReach = null;
            this.minCreativeReach = null;
            this.maxCreativeReach = null;
            this.hitboxMargin = null;
            this.mobFactor = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        AttackRange.Builder builder = AttackRange.attackRange();

        if (this.minReach != null) {
            float reach = this.minReach.getFloat(context);
            builder.minReach(reach);
        }

        if (this.maxReach != null) {
            float reach = this.maxReach.getFloat(context);
            builder.maxReach(reach);
        }

        if (this.minCreativeReach != null) {
            float reach = this.minCreativeReach.getFloat(context);
            builder.minCreativeReach(reach);
        }

        if (this.maxCreativeReach != null) {
            float reach = this.maxCreativeReach.getFloat(context);
            builder.maxCreativeReach(reach);
        }

        if (this.hitboxMargin != null) {
            float margin = this.hitboxMargin.getFloat(context);
            builder.minReach(margin);
        }

        if (this.mobFactor != null) {
            float factor = this.mobFactor.getFloat(context);
            builder.minReach(factor);
        }

        itemStack.setData(DataComponentTypes.ATTACK_RANGE, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.ATTACK_RANGE))
            return;

        AttackRange attackRange = itemStack.getData(DataComponentTypes.ATTACK_RANGE);
        stringBuilder.append("attack-range:\n");
        stringBuilder.append("  min-reach: ").append(attackRange.minReach()).append('\n');
        stringBuilder.append("  max-reach: ").append(attackRange.maxReach()).append('\n');
        stringBuilder.append("  min-creative-reach: ").append(attackRange.minCreativeReach()).append('\n');
        stringBuilder.append("  max-creative-reach: ").append(attackRange.maxCreativeReach()).append('\n');
        stringBuilder.append("  hitbox-margin: ").append(attackRange.hitboxMargin()).append('\n');
        stringBuilder.append("  mob-factor: ").append(attackRange.mobFactor()).append('\n');
    }

} 
