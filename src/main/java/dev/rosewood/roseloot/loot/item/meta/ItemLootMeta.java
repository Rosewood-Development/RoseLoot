package dev.rosewood.roseloot.loot.item.meta;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import dev.rosewood.roseloot.util.OptionalPercentageValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
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
    private NumberProvider enchantmentLevel;
    private boolean includeTreasureEnchantments;
    private boolean uncappedRandomEnchants;
    private List<ItemFlag> hideFlags;
    protected List<Enchantment> randomEnchantments;
    protected List<EnchantmentData> enchantments;
    private List<AttributeData> attributes;
    protected Boolean copyBlockState;
    protected Boolean copyBlockData;
    protected Boolean copyBlockName;

    public ItemLootMeta(ConfigurationSection section) {
        if (section.isString("display-name")) this.displayName = section.getString("display-name");
        if (section.isInt("custom-model-data")) this.customModelData = section.getInt("custom-model-data");
        if (section.isBoolean("unbreakable")) this.unbreakable = section.getBoolean("unbreakable");
        if (section.isInt("repair-cost")) this.repairCost = section.getInt("repair-cost");

        if (section.isList("lore")) {
            this.lore = section.getStringList("lore");
        } else if (section.isString("lore")) {
            this.lore = Collections.singletonList(section.getString("lore"));
        }

        if (section.contains("durability")) {
            if (!section.isConfigurationSection("durability")) {
                // Fixed value
                OptionalPercentageValue durability = OptionalPercentageValue.parse(section.getString("durability"));
                if (durability != null)
                    this.minDurability = durability;
            } else {
                // Min/max values
                ConfigurationSection durabilitySection = section.getConfigurationSection("durability");
                if (durabilitySection != null) {
                    OptionalPercentageValue minDurability = OptionalPercentageValue.parse(durabilitySection.getString("min"));
                    OptionalPercentageValue maxDurability = OptionalPercentageValue.parse(durabilitySection.getString("max"));
                    if (minDurability != null && maxDurability != null) {
                        this.minDurability = minDurability;
                        this.maxDurability = maxDurability;
                    }
                }
            }
        }

        ConfigurationSection enchantRandomlySection = section.getConfigurationSection("enchant-randomly");
        if (enchantRandomlySection != null) {
            this.enchantmentLevel = NumberProvider.fromSection(enchantRandomlySection, "level", 30);
            this.includeTreasureEnchantments = enchantRandomlySection.getBoolean("treasure", false);
            this.uncappedRandomEnchants = enchantRandomlySection.getBoolean("uncapped", false);
        }

        if (section.isBoolean("hide-flags")) {
            if (section.getBoolean("hide-flags"))
                this.hideFlags = Arrays.asList(ItemFlag.values());
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
                this.hideFlags = hideFlags;
        }

        if (section.contains("random-enchantments")) {
            List<String> randomEnchantments = section.getStringList("random-enchantments");
            this.randomEnchantments = new ArrayList<>();
            for (String enchantmentName : randomEnchantments) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(enchantmentName));
                if (enchantment != null)
                    this.randomEnchantments.add(enchantment);
            }
        }

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            List<EnchantmentData> enchantments = new ArrayList<>();
            for (String enchantmentName : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentName);
                NumberProvider levelProvider = NumberProvider.fromSection(enchantmentsSection, enchantmentName, 1);
                enchantments.add(new EnchantmentData(enchantment, levelProvider));
            }
            this.enchantments = enchantments;
        }

        ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
        if (attributesSection != null) {
            List<AttributeData> attributeData = new ArrayList<>();
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

                NumberProvider amount = NumberProvider.fromSection(attributeSection, "amount", 0);

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

                attributeData.add(new AttributeData(attribute, amount, operation, slot));
            }

            this.attributes = attributeData;
        }

        if (section.getBoolean("copy-block-state", false))
            this.copyBlockState = true;

        if (section.getBoolean("copy-block-data", false))
            this.copyBlockData = true;

        if (section.getBoolean("copy-block-name"))
            this.copyBlockName = true;
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

        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            if (this.randomEnchantments != null) {
                List<Enchantment> possibleEnchantments = new ArrayList<>();
                if (!this.randomEnchantments.isEmpty()) {
                    // Not empty, use the suggested
                    possibleEnchantments.addAll(this.randomEnchantments);
                } else {
                    // Empty, pick from every applicable enchantment for the item
                    for (Enchantment enchantment : Enchantment.values())
                        if (enchantment.canEnchantItem(itemStack))
                            possibleEnchantments.add(enchantment);
                }

                Enchantment enchantment = possibleEnchantments.get(LootUtils.RANDOM.nextInt(possibleEnchantments.size()));
                int level = LootUtils.RANDOM.nextInt(enchantment.getMaxLevel()) + 1;
                itemMeta.addEnchant(enchantment, level, true);
            }

            if (this.enchantments != null) {
                for (EnchantmentData enchantmentData : this.enchantments) {
                    int level = enchantmentData.getLevel().getInteger();
                    if (level > 0)
                        itemMeta.addEnchant(enchantmentData.getEnchantment(), level, true);
                }
            }
        }

        if (this.attributes != null) {
            Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
            this.attributes.forEach(x -> attributes.put(x.getAttribute(), x.toAttributeModifier()));
            itemMeta.setAttributeModifiers(attributes);
        }

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

            if (this.copyBlockName != null && this.copyBlockName && block.getState() instanceof Nameable)
                itemMeta.setDisplayName(((Nameable) block.getState()).getCustomName());
        }

        itemStack.setItemMeta(itemMeta);

        if (this.enchantmentLevel != null)
            EnchantingUtils.randomlyEnchant(itemStack, this.enchantmentLevel.getInteger(), this.includeTreasureEnchantments, this.uncappedRandomEnchants);

        return itemStack;
    }

    public static ItemLootMeta fromSection(Material material, ConfigurationSection section) {
        if (Tag.ITEMS_BANNERS.isTagged(material))
            return new BannerItemLootMeta(section);

        switch (material) {
            case WRITABLE_BOOK:
            case WRITTEN_BOOK:
                return new BookItemLootMeta(section);
            case ENCHANTED_BOOK:
                return new EnchantmentStorageItemLootMeta(section);
            case FIREWORK_STAR:
                return new FireworkEffectItemLootMeta(section);
            case FIREWORK_ROCKET:
                return new FireworkItemLootMeta(section);
            case KNOWLEDGE_BOOK:
                return new KnowledgeBookItemLootMeta(section);
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case LEATHER_HORSE_ARMOR:
                return new LeatherArmorItemLootMeta(section);
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
                return new PotionItemLootMeta(section);
            case PLAYER_HEAD:
                return new SkullItemLootMeta(section);
            case SUSPICIOUS_STEW:
                return new SuspiciousStewItemLootMeta(section);
            case TROPICAL_FISH_BUCKET:
                return new TropicalFishBucketItemLootMeta(section);
            case AXOLOTL_BUCKET:
                return new AxolotlBucketItemLootMeta(section);
            case BUNDLE:
                return new BundleItemLootMeta(section);
            default:
                return new ItemLootMeta(section);
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        Material material = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (itemMeta.hasDisplayName()) stringBuilder.append("display-name: '").append(LootUtils.decolorize(itemMeta.getDisplayName())).append('\'').append('\n');
        if (itemMeta.hasCustomModelData()) stringBuilder.append("custom-model-data: ").append(itemMeta.getCustomModelData()).append('\n');
        if (itemMeta.isUnbreakable()) stringBuilder.append("unbreakable: true\n");

        if (itemMeta instanceof Repairable) {
            Repairable repairable = (Repairable) itemMeta;
            if (repairable.hasRepairCost())
                stringBuilder.append("repair-cost: ").append(repairable.getRepairCost()).append('\n');
        }

        if (itemMeta instanceof Damageable) {
            Damageable damageable = (Damageable) itemMeta;
            if (damageable.hasDamage())
                stringBuilder.append("durability: ").append(itemStack.getType().getMaxDurability() - damageable.getDamage()).append('\n');
        }

        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            stringBuilder.append("lore:\n");
            for (String line : itemMeta.getLore())
                stringBuilder.append("  - '").append(LootUtils.decolorize(line)).append("'");
        }

        Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
        if (itemFlags.size() == ItemFlag.values().length) {
            stringBuilder.append("hide-flags: true\n");
        } else if (!itemFlags.isEmpty()) {
            stringBuilder.append("hide-flags:\n");
            for (ItemFlag itemFlag : itemFlags)
                stringBuilder.append(itemFlag.name().toLowerCase()).append('\n');
        }

        if (!itemMeta.getEnchants().isEmpty() && material != Material.ENCHANTED_BOOK) {
            stringBuilder.append("enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemMeta.getEnchants().entrySet())
                stringBuilder.append("  ").append(entry.getKey().getKey().getKey()).append(": ").append(entry.getValue());
        }

        Multimap<Attribute, AttributeModifier> attributes = itemMeta.getAttributeModifiers();
        if (attributes != null) {
            stringBuilder.append("attributes:\n");
            int i = 0;
            for (Attribute attribute : attributes.keySet()) {
                for (AttributeModifier modifier : attributes.get(attribute)) {
                    stringBuilder.append("  ").append(i++).append(":\n");
                    stringBuilder.append("    ").append("name: ").append('\'').append(attribute.getKey().getKey()).append('\'');
                    stringBuilder.append("    ").append("amount: ").append(modifier.getAmount()).append('\n');
                    stringBuilder.append("    ").append("operation: ").append(modifier.getOperation().name().toLowerCase()).append('\'');
                    if (modifier.getSlot() != null)
                        stringBuilder.append("    ").append("slot: ").append(modifier.getSlot().name().toLowerCase()).append('\'');
                }
            }
        }

        if (Tag.ITEMS_BANNERS.isTagged(material))
            BannerItemLootMeta.applyProperties(itemStack, stringBuilder);

        switch (material) {
            case WRITABLE_BOOK:
            case WRITTEN_BOOK:
                BookItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case ENCHANTED_BOOK:
                EnchantmentStorageItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case FIREWORK_STAR:
                FireworkEffectItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case FIREWORK_ROCKET:
                FireworkItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case KNOWLEDGE_BOOK:
                KnowledgeBookItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case LEATHER_HORSE_ARMOR:
                LeatherArmorItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
                PotionItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case PLAYER_HEAD:
                SkullItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case SUSPICIOUS_STEW:
                SuspiciousStewItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case TROPICAL_FISH_BUCKET:
                TropicalFishBucketItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case AXOLOTL_BUCKET:
                AxolotlBucketItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
            case BUNDLE:
                BundleItemLootMeta.applyProperties(itemStack, stringBuilder);
                break;
        }
    }

    private static class AttributeData {

        private final Attribute attribute;
        private final NumberProvider amount;
        private final AttributeModifier.Operation operation;
        private final EquipmentSlot slot;

        private AttributeData(Attribute attribute, NumberProvider amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
            this.attribute = attribute;
            this.amount = amount;
            this.operation = operation;
            this.slot = slot;
        }

        public Attribute getAttribute() {
            return this.attribute;
        }

        public AttributeModifier toAttributeModifier() {
            return new AttributeModifier(UUID.randomUUID(), this.attribute.getKey().getKey(), this.amount.getDouble(), this.operation, this.slot);
        }

    }

    protected static class EnchantmentData {

        private final Enchantment enchantment;
        private final NumberProvider level;

        public EnchantmentData(Enchantment enchantment, NumberProvider level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public NumberProvider getLevel() {
            return this.level;
        }

    }

}
