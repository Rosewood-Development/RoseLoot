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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.Material;
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

        if (!(itemStack.getItemMeta() instanceof EnchantmentStorageMeta itemMeta))
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
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            for (EnchantmentData enchantmentData : this.enchantments) {
                int level = enchantmentData.level().getInteger(context);
                if (level > 0)
                    enchantments.put(enchantmentData.enchantment(), level);
            }

            if (this.enchantmentsAmount == null) {
                enchantments.forEach((enchantment, level) -> itemMeta.addStoredEnchant(enchantment, level, true));
            } else {
                int enchantmentsAmount = this.enchantmentsAmount.getInteger(context);
                List<Enchantment> keys = new ArrayList<>(enchantments.keySet());
                Collections.shuffle(keys);
                for (int i = 0; i < enchantmentsAmount && !keys.isEmpty(); i++) {
                    Enchantment enchantment = keys.removeFirst();
                    int level = enchantments.get(enchantment);
                    itemMeta.addStoredEnchant(enchantment, level, true);
                }
            }
        }

        if (this.removeEnchantments != null) {
            for (String key : this.removeEnchantments.getList(context)) {
                Enchantment enchantment = VersionUtils.getEnchantment(key);
                if (enchantment != null)
                    itemMeta.removeStoredEnchant(enchantment);
            }
        }

        itemStack.setItemMeta(itemMeta);

        if (itemMeta.getStoredEnchants().isEmpty()) {
            if (NMSUtil.isPaper()) {
                itemStack = itemStack.withType(Material.BOOK);
            } else {
                itemStack.setType(Material.BOOK);
            }
        }

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof EnchantmentStorageMeta itemMeta))
            return;

        if (!itemMeta.getStoredEnchants().isEmpty()) {
            stringBuilder.append("enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemMeta.getStoredEnchants().entrySet())
                stringBuilder.append("  ").append(entry.getKey().getKey().getKey()).append(": ").append(entry.getValue());
        }
    }

}
