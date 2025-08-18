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
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

class StoredEnchantmentsComponent implements LootItemComponent {

    private final Map<Enchantment, NumberProvider> enchantments;
    private final boolean showInTooltip;

    public StoredEnchantmentsComponent(ConfigurationSection section) {
        ConfigurationSection enchantmentsSection = section.getConfigurationSection("stored-enchantments");
        if (enchantmentsSection != null) {
            this.enchantments = new HashMap<>();
            ConfigurationSection enchantmentsListSection = enchantmentsSection.getConfigurationSection("enchantments");
            Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            if (enchantmentsListSection != null) {
                for (String key : enchantmentsListSection.getKeys(false)) {
                    NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
                    Enchantment enchantment = registry.get(namespacedKey);
                    if (enchantment != null)
                        this.enchantments.put(enchantment, NumberProvider.fromSection(enchantmentsListSection, key, 1));
                }
            }
            this.showInTooltip = enchantmentsSection.getBoolean("show-in-tooltip", true);
        } else {
            this.enchantments = null;
            this.showInTooltip = true;
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
                itemStack.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments(enchantmentLevels, this.showInTooltip));
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.STORED_ENCHANTMENTS))
            return;

        ItemEnchantments itemEnchantments = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS);
        if (!itemEnchantments.enchantments().isEmpty()) {
            stringBuilder.append("stored-enchantments:\n");
            stringBuilder.append("  show-in-tooltip: ").append(itemEnchantments.showInTooltip()).append('\n');
            stringBuilder.append("  enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.enchantments().entrySet())
                stringBuilder.append("    ").append(entry.getKey().getKey().asMinimalString()).append(": ").append(entry.getValue()).append('\n');
        }
    }

} 
