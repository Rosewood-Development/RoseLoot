package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

class EnchantmentsComponent implements LootItemComponent {

    private final Map<Enchantment, NumberProvider> enchantments;
    private final Boolean showInTooltip;

    public EnchantmentsComponent(ConfigurationSection section) {
        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            ConfigurationSection levelsSection = enchantmentsSection.getConfigurationSection("levels");
            if (levelsSection != null) {
                this.enchantments = new HashMap<>();
                for (String key : levelsSection.getKeys(false)) {
                    Key namespacedKey = Key.key(key);
                    Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(namespacedKey);
                    NumberProvider levelProvider = NumberProvider.fromSection(levelsSection, key, null);
                    if (enchantment != null && levelProvider != null)
                        this.enchantments.put(enchantment, levelProvider);
                }
            } else {
                this.enchantments = null;
            }

            if (enchantmentsSection.isBoolean("show-in-tooltip")) {
                this.showInTooltip = enchantmentsSection.getBoolean("show-in-tooltip");
            } else {
                this.showInTooltip = null;
            }
        } else {
            this.enchantments = null;
            this.showInTooltip = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();

        if (this.enchantments != null)
            this.enchantments.forEach((key, value) -> builder.add(key, value.getInteger(context)));

        if (this.showInTooltip != null)
            builder.showInTooltip(this.showInTooltip);

        itemStack.setData(DataComponentTypes.ENCHANTMENTS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.ENCHANTMENTS))
            return;

        ItemEnchantments itemEnchantments = itemStack.getData(DataComponentTypes.ENCHANTMENTS);
        var enchantments = itemEnchantments.enchantments();
        if (!enchantments.isEmpty()) {
            stringBuilder.append("enchantments:\n");
            stringBuilder.append("  levels:\n");
            for (var entry : enchantments.entrySet()) {
                String name = entry.getKey().getKey().asMinimalString();
                if (name.contains(":")) {
                    stringBuilder.append("    '").append(name).append("': ").append(entry.getValue()).append('\n');
                } else {
                    stringBuilder.append("    ").append(name).append(": ").append(entry.getValue()).append('\n');
                }
            }
            stringBuilder.append("  show-in-tooltip: ").append(itemEnchantments.showInTooltip()).append('\n');
        }
    }

}
