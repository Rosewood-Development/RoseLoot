package dev.rosewood.roseloot.loot.item.component.common.stable;

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

public class EnchantmentsComponent implements LootItemComponent {

    private final Map<Enchantment, NumberProvider> enchantments;

    public EnchantmentsComponent(ConfigurationSection section) {
        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            this.enchantments = new HashMap<>();
            for (String key : enchantmentsSection.getKeys(false)) {
                Key namespacedKey = Key.key(key);
                Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(namespacedKey);
                NumberProvider levelProvider = NumberProvider.fromSection(enchantmentsSection, key, null);
                if (enchantment != null && levelProvider != null)
                    this.enchantments.put(enchantment, levelProvider);
            }
        } else {
            this.enchantments = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();

        if (this.enchantments != null)
            this.enchantments.forEach((key, value) -> builder.add(key, value.getInteger(context)));

        itemStack.setData(DataComponentTypes.ENCHANTMENTS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.ENCHANTMENTS))
            return;

        ItemEnchantments itemEnchantments = itemStack.getData(DataComponentTypes.ENCHANTMENTS);
        var enchantments = itemEnchantments.enchantments();
        if (!enchantments.isEmpty()) {
            stringBuilder.append("enchantments:\n");
            for (var entry : enchantments.entrySet()) {
                String name = entry.getKey().getKey().asMinimalString();
                if (name.contains(":")) {
                    stringBuilder.append("  '").append(name).append("': ").append(entry.getValue()).append('\n');
                } else {
                    stringBuilder.append("  ").append(name).append(": ").append(entry.getValue()).append('\n');
                }
            }
        }
    }

}
