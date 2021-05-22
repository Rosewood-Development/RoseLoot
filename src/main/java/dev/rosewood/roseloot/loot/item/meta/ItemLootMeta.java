package dev.rosewood.roseloot.loot.item.meta;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.OptionalPercentageValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class ItemLootMeta {

    private String displayName;
    private List<String> lore;
    private Integer customModelData;
    private Boolean unbreakable;
    private Integer repairCost;
    private OptionalPercentageValue minDurability, maxDurability;
    private List<ItemFlag> hideFlags;
    private Map<Enchantment, Integer> enchantments;
    private Multimap<Attribute, AttributeModifier> attributes;

    protected Boolean copyBlockState;
    protected Boolean copyBlockData;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public void setDurability(OptionalPercentageValue minDurability, OptionalPercentageValue maxDurability) {
        this.minDurability = minDurability;
        this.maxDurability = maxDurability;
    }

    public void setHideFlags(List<ItemFlag> hideFlags) {
        this.hideFlags = hideFlags;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setAttributes(Multimap<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
    }

    public void setCopyBlockState(boolean copyBlockState) {
        this.copyBlockState = copyBlockState;
    }

    public void setCopyBlockData(boolean copyBlockData) {
        this.copyBlockData = copyBlockData;
    }

    /**
     * Applies stored ItemMeta information to the given ItemStack
     *
     * @param itemStack The ItemStack to apply ItemMeta to
     * @param context The LootContext
     * @return The same ItemStack
     */
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.displayName != null) itemMeta.setDisplayName(HexUtils.colorify(this.displayName));
        if (this.lore != null) itemMeta.setLore(this.lore.stream().map(HexUtils::colorify).collect(Collectors.toList()));
        if (this.customModelData != null && NMSUtil.getVersionNumber() > 13) itemMeta.setCustomModelData(this.customModelData);
        if (this.unbreakable != null) itemMeta.setUnbreakable(this.unbreakable);
        if (this.hideFlags != null) itemMeta.addItemFlags(this.hideFlags.toArray(new ItemFlag[0]));
        if (this.enchantments != null) this.enchantments.forEach((x, y) -> itemMeta.addEnchant(x, y, true));
        if (this.attributes != null) itemMeta.setAttributeModifiers(this.attributes);

        if (itemMeta instanceof Damageable && this.minDurability != null) {
            Damageable damageable = (Damageable) itemMeta;
            int max = itemStack.getType().getMaxDurability();
            if (this.maxDurability == null) {
                // Set fixed durability value
                int durability = this.minDurability.getAsInt(max);
                damageable.setDamage(itemStack.getType().getMaxDurability() - durability);
            } else {
                // Set random durability in range
                int minDurability = this.minDurability.getAsInt(max);
                int maxDurability = this.maxDurability.getAsInt(max);
                damageable.setDamage(itemStack.getType().getMaxDurability() - LootUtils.randomInRange(minDurability, maxDurability));
            }
        }

        if (this.repairCost != null && itemMeta instanceof Repairable)
            ((Repairable) itemMeta).setRepairCost(this.repairCost);

        Block block = context.getLootedBlock();
        if (block != null && block.getType() == itemStack.getType()) {
            if (this.copyBlockState != null && this.copyBlockState && itemMeta instanceof BlockStateMeta)
                ((BlockStateMeta) itemMeta).setBlockState(block.getState());

            if (this.copyBlockData != null && this.copyBlockData && itemMeta instanceof BlockDataMeta)
                ((BlockDataMeta) itemMeta).setBlockData(block.getBlockData());
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemLootMeta fromSection(Material material, ConfigurationSection section) {
        ItemLootMeta meta;
        switch (material) {
            default:
                meta = new ItemLootMeta();
                break;
        }

        if (section.isString("display-name")) meta.setDisplayName(section.getString("display-name"));
        if (section.isInt("custom-model-data")) meta.setCustomModelData(section.getInt("custom-model-data"));
        if (section.isBoolean("unbreakable")) meta.setUnbreakable(section.getBoolean("unbreakable"));
        if (section.isInt("repair-cost")) meta.setRepairCost(section.getInt("repair-cost"));

        if (section.isList("lore")) {
            meta.setLore(section.getStringList("lore"));
        } else if (section.isString("lore")) {
            meta.setLore(Collections.singletonList(section.getString("lore")));
        }

        if (section.contains("durability")) {
            if (!section.isConfigurationSection("durability")) {
                // Fixed value
                OptionalPercentageValue durability = OptionalPercentageValue.parse(section.getString("durability"));
                if (durability != null)
                    meta.setDurability(durability, null);
            } else {
                // Min/max values
                ConfigurationSection durabilitySection = section.getConfigurationSection("durability");
                if (durabilitySection != null) {
                    OptionalPercentageValue minDurability = OptionalPercentageValue.parse(durabilitySection.getString("min"));
                    OptionalPercentageValue maxDurability = OptionalPercentageValue.parse(durabilitySection.getString("max"));
                    if (minDurability != null && maxDurability != null)
                        meta.setDurability(minDurability, maxDurability);
                }
            }
        }

        if (section.isBoolean("hide-flags")) {
            if (section.getBoolean("hide-flags"))
                meta.setHideFlags(Arrays.asList(ItemFlag.values()));
        } else if (section.isList("hide-flags")) {
            List<String> flagNames = section.getStringList("hide-flags");
            List<ItemFlag> hideFlags = new ArrayList<>();
            outer:
            for (ItemFlag value : ItemFlag.values()) {
                for (String flagName : flagNames) {
                    if (value.name().toLowerCase().contains(flagName.toLowerCase())) {
                        hideFlags.add(value);
                        continue outer;
                    }
                }
            }

            if (!flagNames.isEmpty())
                meta.setHideFlags(hideFlags);
        }

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            for (String enchantmentName : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
                int level = enchantmentsSection.getInt(enchantmentName, 1);
                enchantments.put(enchantment, level);
            }
            meta.setEnchantments(enchantments);
        }

        ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
        if (attributesSection != null) {
            Multimap<Attribute, AttributeModifier> attributeModifiers = ArrayListMultimap.create();
            for (String key : attributesSection.getKeys(false)) {
                ConfigurationSection attributeSection = attributesSection.getConfigurationSection(key);
                if (attributeSection == null)
                    continue;

                String name = attributeSection.getString("name");
                if (name == null || name.isEmpty())
                    continue;

                NamespacedKey nameKey = NamespacedKey.fromString(name.toLowerCase());
                Attribute attribute = null;
                for (Attribute value : Attribute.values()) {
                    if (value.getKey().equals(nameKey)) {
                        attribute = value;
                        break;
                    }
                }

                if (attribute == null)
                    continue;

                double amount = attributeSection.getDouble("amount", 0);

                String operationName = attributeSection.getString("operation");
                if (operationName == null)
                    continue;

                AttributeModifier.Operation operation = null;
                for (AttributeModifier.Operation value : AttributeModifier.Operation.values()) {
                    if (value.name().equalsIgnoreCase(operationName)) {
                        operation = value;
                        break;
                    }
                }

                if (operation == null)
                    break;

                String slotName = attributeSection.getString("slot");
                EquipmentSlot slot = null;
                if (slotName != null) {
                    for (EquipmentSlot value : EquipmentSlot.values()) {
                        if (value.name().equalsIgnoreCase(slotName)) {
                            slot = value;
                            break;
                        }
                    }
                }

                attributeModifiers.put(attribute, new AttributeModifier(UUID.randomUUID(), attribute.getKey().getKey(), amount, operation, slot));
            }

            meta.setAttributes(attributeModifiers);
        }

        if (section.getBoolean("copy-block-state", false))
            meta.setCopyBlockState(true);

        if (section.getBoolean("copy-block-data", false))
            meta.setCopyBlockData(true);

        return meta;
    }

}
