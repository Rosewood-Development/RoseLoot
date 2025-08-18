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
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class StoredEnchantmentsComponent implements LootItemComponent {

    private final Map<Enchantment, NumberProvider> enchantments;

    public StoredEnchantmentsComponent(ConfigurationSection section) {
        ConfigurationSection enchantmentsSection = section.getConfigurationSection("stored-enchantments");
        if (enchantmentsSection != null) {
            this.enchantments = new HashMap<>();
            Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            for (String key : enchantmentsSection.getKeys(false)) {
                NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
                Enchantment enchantment = registry.get(namespacedKey);
                if (enchantment != null)
                    this.enchantments.put(enchantment, NumberProvider.fromSection(enchantmentsSection, key, 1));
            }
        } else {
            this.enchantments = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.enchantments != null && !this.enchantments.isEmpty()) {
            Map<Enchantment, Integer> enchantmentLevels = new HashMap<>();
            for (Map.Entry<Enchantment, NumberProvider> entry : this.enchantments.entrySet()) {
                int level = entry.getValue().getInteger(context);
                if (level > 0) {
                    enchantmentLevels.put(entry.getKey(), level);
                }
            }
            if (!enchantmentLevels.isEmpty()) {
                itemStack.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments(enchantmentLevels));
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.STORED_ENCHANTMENTS))
            return;

        ItemEnchantments itemEnchantments = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS);
        if (!itemEnchantments.enchantments().isEmpty()) {
            stringBuilder.append("stored-enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.enchantments().entrySet()) {
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
