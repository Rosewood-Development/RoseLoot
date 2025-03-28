package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class ConsumableComponent implements LootItemComponent {

    private final NumberProvider consumeSeconds;
    private final ItemUseAnimation animation;
    private final StringProvider sound;
    private final Boolean hasConsumeParticles;
    private final List<EffectConfig> effects;

    public ConsumableComponent(ConfigurationSection section) {
        ConfigurationSection consumableSection = section.getConfigurationSection("consumable");
        if (consumableSection != null) {
            this.consumeSeconds = NumberProvider.fromSection(consumableSection, "consume-seconds", null);
            this.animation = ItemUseAnimation.valueOf(consumableSection.getString("animation", "EAT").toUpperCase());
            this.sound = StringProvider.fromSection(consumableSection, "sound", null);
            if (consumableSection.isBoolean("has-consume-particles")) {
                this.hasConsumeParticles = consumableSection.getBoolean("has-consume-particles");
            } else {
                this.hasConsumeParticles = null;
            }
            this.effects = ParsingUtils.parseEffectConfigs(consumableSection.getConfigurationSection("effects"));
        } else {
            this.consumeSeconds = null;
            this.animation = null;
            this.sound = null;
            this.hasConsumeParticles = false;
            this.effects = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        Consumable.Builder builder = Consumable.consumable();

        if (this.consumeSeconds != null)
            builder.consumeSeconds((float) this.consumeSeconds.getDouble(context));

        if (this.animation != null)
            builder.animation(this.animation);

        if (this.sound != null)
            builder.sound(Key.key(this.sound.get(context).toLowerCase()));

        if (this.hasConsumeParticles != null)
            builder.hasConsumeParticles(this.hasConsumeParticles);

        if (this.effects != null)
            builder.addEffects(ParsingUtils.translateEffects(this.effects, context));

        itemStack.setData(DataComponentTypes.CONSUMABLE, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.CONSUMABLE))
            return;

        Consumable consumable = itemStack.getData(DataComponentTypes.CONSUMABLE);
        stringBuilder.append("consumable:\n");
        stringBuilder.append("  consume-seconds: ").append(consumable.consumeSeconds()).append('\n');
        stringBuilder.append("  animation: ").append(consumable.animation().name().toLowerCase()).append('\n');
        stringBuilder.append("  sound: '").append(consumable.sound().asMinimalString()).append("'\n");
        stringBuilder.append("  has-consume-particles: ").append(consumable.hasConsumeParticles()).append('\n');

        ParsingUtils.applyProperties(consumable.consumeEffects(), 2, stringBuilder);
    }

} 
