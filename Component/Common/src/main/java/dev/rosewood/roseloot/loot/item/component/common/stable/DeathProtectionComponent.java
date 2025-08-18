package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.component.common.EffectConfig;
import dev.rosewood.roseloot.loot.item.component.common.ParsingUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class DeathProtectionComponent implements LootItemComponent {

    private final List<EffectConfig> deathEffects;

    public DeathProtectionComponent(ConfigurationSection section) {
        ConfigurationSection deathProtectionSection = section.getConfigurationSection("death-protection");
        if (deathProtectionSection != null) {
            this.deathEffects = ParsingUtils.parseEffectConfigs(deathProtectionSection.getConfigurationSection("effects"));
        } else {
            this.deathEffects = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.deathEffects != null) {
            List<ConsumeEffect> effects = this.deathEffects.stream().map(x -> x.toConsumeEffect(context)).toList();
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection(effects));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.DEATH_PROTECTION))
            return;

        DeathProtection deathProtection = itemStack.getData(DataComponentTypes.DEATH_PROTECTION);
        if (!deathProtection.deathEffects().isEmpty()) {
            stringBuilder.append("death-protection:\n");
            ParsingUtils.applyEffectProperties(deathProtection.deathEffects(), 2, stringBuilder);
        }
    }

} 
