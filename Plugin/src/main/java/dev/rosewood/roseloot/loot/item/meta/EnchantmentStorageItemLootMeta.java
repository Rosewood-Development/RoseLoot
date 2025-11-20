package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VersionUtils;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EnchantmentStorageItemLootMeta extends ItemLootMeta {

    public EnchantmentStorageItemLootMeta(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.randomEnchantments != null) {
            List<Enchantment> enchantmentsSource = new ArrayList<>();
            List<String> enchantmentKeys = this.randomEnchantments.getList(context);
            for (String key : enchantmentKeys) {
                if (key.startsWith("#")) {
                    if (!NMSUtil.isPaper() && NMSUtil.getVersionNumber() < 21) {
                        RoseLoot.getInstance().getLogger().warning("Enchantment tag key '" + key + "' was provided but tags are only supported on Paper servers running 1.21 or newer.");
                        continue;
                    }

                    try {
                        TagKey<Enchantment> tagKey = TagKey.create(RegistryKey.ENCHANTMENT, key.substring(1));
                        enchantmentsSource.addAll(Registry.ENCHANTMENT.getTagValues(tagKey));
                    } catch (Exception e) {
                        RoseLoot.getInstance().getLogger().warning("Enchantment tag key '" + key + "' was provided but is invalid.");
                    }
                } else {
                    Enchantment enchantment = VersionUtils.getEnchantment(key);
                    if (enchantment != null)
                        enchantmentsSource.add(enchantment);
                }
            }
            if (enchantmentsSource.isEmpty() && enchantmentKeys.isEmpty()) {
                // Empty, pick from every applicable enchantment for the item
                enchantmentsSource = Arrays.asList(VersionUtils.getEnchantments());
            }

            int amount = this.randomEnchantmentsAmount.getInteger(context);
            for (int i = 0; i < amount; i++) {
                enchantmentsSource = enchantmentsSource.stream().filter(Predicate.not(itemMeta::hasConflictingStoredEnchant)).toList();
                if (enchantmentsSource.isEmpty())
                    break;

                Enchantment enchantment = enchantmentsSource.get(LootUtils.RANDOM.nextInt(enchantmentsSource.size()));
                int level = LootUtils.RANDOM.nextInt(enchantment.getMaxLevel()) + 1;
                itemMeta.addStoredEnchant(enchantment, level, false);
            }
        }

        if (this.enchantments != null) {
            for (EnchantmentData enchantmentData : this.enchantments) {
                int level = enchantmentData.level().getInteger(context);
                if (level > 0)
                    itemMeta.addStoredEnchant(enchantmentData.enchantment(), level, true);
            }
        }

        if (this.removeEnchantments != null) {
            for (String key : this.removeEnchantments.getList(context)) {
                Enchantment enchantment = VersionUtils.getEnchantment(key);
                if (enchantment != null)
                    itemMeta.removeEnchant(enchantment);
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (!itemMeta.getStoredEnchants().isEmpty()) {
            stringBuilder.append("enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemMeta.getStoredEnchants().entrySet())
                stringBuilder.append("  ").append(entry.getKey().getKey().getKey()).append(": ").append(entry.getValue());
        }
    }

}
