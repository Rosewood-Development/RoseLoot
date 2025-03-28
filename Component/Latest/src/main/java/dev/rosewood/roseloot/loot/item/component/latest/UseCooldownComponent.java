package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class UseCooldownComponent implements LootItemComponent {

    private final NumberProvider seconds;
    private final StringProvider cooldownGroup;

    public UseCooldownComponent(ConfigurationSection section) {
        ConfigurationSection useCooldownSection = section.getConfigurationSection("use-cooldown");
        if (useCooldownSection != null) {
            this.seconds = NumberProvider.fromSection(useCooldownSection, "seconds", null);
            this.cooldownGroup = StringProvider.fromSection(useCooldownSection, "cooldown-group", null);
        } else {
            this.seconds = null;
            this.cooldownGroup = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.seconds != null) {
            float secondsValue = (float) this.seconds.getDouble(context);
            if (secondsValue > 0) {
                UseCooldown.Builder builder = UseCooldown.useCooldown(secondsValue);
                
                if (this.cooldownGroup != null) {
                    String group = this.cooldownGroup.get(context);
                    if (group != null && !group.isEmpty())
                        builder.cooldownGroup(Key.key(group));
                }
                
                itemStack.setData(DataComponentTypes.USE_COOLDOWN, builder.build());
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.USE_COOLDOWN))
            return;

        UseCooldown useCooldown = itemStack.getData(DataComponentTypes.USE_COOLDOWN);
        stringBuilder.append("use-cooldown:\n");
        stringBuilder.append("  seconds: ").append(useCooldown.seconds()).append('\n');
        
        Key cooldownGroup = useCooldown.cooldownGroup();
        if (cooldownGroup != null) {
            stringBuilder.append("  cooldown-group: '").append(cooldownGroup.asMinimalString()).append("'\n");
        }
    }

} 
