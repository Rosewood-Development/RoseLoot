package dev.rosewood.roseloot.loot.item.meta;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.BlockInfo;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.nms.EnchantingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

    private static class MaterialMappings {
        private static final Map<Material, Function<ConfigurationSection, ? extends ItemLootMeta>> CONSTRUCTORS;
        private static final Map<Material, BiConsumer<ItemStack, StringBuilder>> PROPERTY_APPLIERS;
        static {
            CONSTRUCTORS = new HashMap<>();
            PROPERTY_APPLIERS = new HashMap<>();

            mapMaterials(BookItemLootMeta::new, BookItemLootMeta::applyProperties, Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);
            mapMaterials(EnchantmentStorageItemLootMeta::new, EnchantmentStorageItemLootMeta::applyProperties, Material.ENCHANTED_BOOK);
            mapMaterials(FireworkEffectItemLootMeta::new, FireworkEffectItemLootMeta::applyProperties, Material.FIREWORK_STAR);
            mapMaterials(FireworkItemLootMeta::new, FireworkItemLootMeta::applyProperties, Material.FIREWORK_ROCKET);
            mapMaterials(KnowledgeBookItemLootMeta::new, KnowledgeBookItemLootMeta::applyProperties, Material.KNOWLEDGE_BOOK);

            mapMaterials(BannerItemLootMeta::new, BannerItemLootMeta::applyProperties, Tag.ITEMS_BANNERS.getValues().toArray(Material[]::new));

            Material[] leatherArmor = { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS };
            if (NMSUtil.getVersionNumber() >= 20) {
                mapMaterials(ArmorItemLootMeta::new, ArmorItemLootMeta::applyProperties, Tag.ITEMS_TRIMMABLE_ARMOR.getValues().toArray(Material[]::new));
                mapMaterials(ColorableArmorItemLootMeta::new, ColorableArmorItemLootMeta::applyProperties, leatherArmor); // overwrites the above
            } else {
                mapMaterials(LeatherArmorItemLootMeta::new, LeatherArmorItemLootMeta::applyProperties, ObjectArrays.concat(leatherArmor, Material.LEATHER_HORSE_ARMOR));
            }
        }

        private static void mapMaterials(Function<ConfigurationSection, ? extends ItemLootMeta> constructor, BiConsumer<ItemStack, StringBuilder> propertyApplier, Material... materials) {
            for (Material material : materials) {
                CONSTRUCTORS.put(material, constructor);
                PROPERTY_APPLIERS.put(material, propertyApplier);
            }
        }
    }

    private final StringProvider displayName;
    private final StringProvider lore;
    private Integer customModelData;
    private Boolean unbreakable;
    private Integer repairCost;
    private final NumberProvider durability;
    private NumberProvider enchantmentLevel;
    private boolean includeTreasureEnchantments;
    private List<ItemFlag> hideFlags;
    protected List<Enchantment> randomEnchantments;
    protected List<EnchantmentData> enchantments;
    private List<AttributeData> attributes;
    protected Boolean copyBlockState;
    protected Boolean copyBlockData;
    protected Boolean copyBlockName;

    public ItemLootMeta(ConfigurationSection section) {
        this.displayName = StringProvider.fromSection(section, "display-name", null);
        this.lore = StringProvider.fromSection(section, "lore", null);
        if (section.isInt("custom-model-data")) this.customModelData = section.getInt("custom-model-data");
        if (section.isBoolean("unbreakable")) this.unbreakable = section.getBoolean("unbreakable");
        if (section.isInt("repair-cost")) this.repairCost = section.getInt("repair-cost");
        this.durability = NumberProvider.fromSection(section, "durability", null);

        ConfigurationSection enchantRandomlySection = section.getConfigurationSection("enchant-randomly");
        if (enchantRandomlySection != null) {
            this.enchantmentLevel = NumberProvider.fromSection(enchantRandomlySection, "level", 30);
            this.includeTreasureEnchantments = enchantRandomlySection.getBoolean("treasure", false);
        }

        if (section.isBoolean("hide-flags")) {
            if (section.getBoolean("hide-flags"))
                this.hideFlags = List.of(ItemFlag.values());
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
                if (enchantment == null)
                    continue;

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

        if (section.getBoolean("copy-block-name", false))
            this.copyBlockName = true;
    }

    /**
     * Applies stored ItemMeta information to the given ItemStack
     *
     * @param itemStack The ItemStack to apply ItemMeta to
     * @param context The LootContext, nullable
     * @return The same ItemStack
     */
    @SuppressWarnings("deprecation")
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.displayName != null) itemMeta.setDisplayName(this.displayName.getFormatted(context));
        if (this.lore != null) itemMeta.setLore(this.lore.getListFormatted(context));
        if (this.customModelData != null) itemMeta.setCustomModelData(this.customModelData);
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
                        if (enchantment.canEnchantItem(itemStack) && enchantment.isDiscoverable())
                            possibleEnchantments.add(enchantment);
                }

                if (!possibleEnchantments.isEmpty()) {
                    Enchantment enchantment = possibleEnchantments.get(LootUtils.RANDOM.nextInt(possibleEnchantments.size()));
                    int level = LootUtils.RANDOM.nextInt(enchantment.getMaxLevel()) + 1;
                    itemMeta.addEnchant(enchantment, level, true);
                }
            }

            if (this.enchantments != null) {
                for (EnchantmentData enchantmentData : this.enchantments) {
                    int level = enchantmentData.level().getInteger(context);
                    if (level > 0)
                        itemMeta.addEnchant(enchantmentData.enchantment(), level, true);
                }
            }
        }

        if (this.attributes != null) {
            Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
            this.attributes.forEach(x -> attributes.put(x.attribute(), x.toAttributeModifier(context)));
            itemMeta.setAttributeModifiers(attributes);
        }

        if (itemMeta instanceof Damageable damageable && this.durability != null) {
            int max = itemStack.getType().getMaxDurability();
            int durabilityValue;
            if (this.durability.isPercentage()) {
                durabilityValue = (int) Math.round(this.durability.getDouble(context) * max);
            } else {
                durabilityValue = this.durability.getInteger(context);
            }
            damageable.setDamage(max - Math.max(0, Math.min(durabilityValue, max)));
        }

        if (this.repairCost != null && itemMeta instanceof Repairable)
            ((Repairable) itemMeta).setRepairCost(this.repairCost);

        Optional<BlockInfo> lootedBlock = context.getLootedBlockInfo();
        if (lootedBlock.isPresent() && lootedBlock.get().getMaterial() == itemStack.getType()) {
            BlockInfo block = lootedBlock.get();
            if (this.copyBlockState != null && this.copyBlockState && itemMeta instanceof BlockStateMeta blockStateMeta)
                blockStateMeta.setBlockState(block.getState());

            if (this.copyBlockData != null && this.copyBlockData && itemMeta instanceof BlockDataMeta blockDataMeta)
                blockDataMeta.setBlockData(block.getData());

            if (this.copyBlockName != null && this.copyBlockName && block.getState() instanceof Nameable nameable)
                itemMeta.setDisplayName(nameable.getCustomName());
        }

        itemStack.setItemMeta(itemMeta);

        if (this.enchantmentLevel != null) {
            Optional<World> world = context.get(LootContextParams.ORIGIN).map(Location::getWorld);
            itemStack = EnchantingUtils.randomlyEnchant(itemStack, this.enchantmentLevel.getInteger(context), this.includeTreasureEnchantments, world.orElse(null));
        }

        return itemStack;
    }

    public static ItemLootMeta fromSection(Material material, ConfigurationSection section) {
        if (Tag.ITEMS_BANNERS.isTagged(material))
            return new BannerItemLootMeta(section);

        if (NMSUtil.getVersionNumber() >= 20 && Tag.ITEMS_TRIMMABLE_ARMOR.isTagged(material)) {
            return switch (material) {
                case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> new ColorableArmorItemLootMeta(section);
                default -> new ArmorItemLootMeta(section);
            };
        }

        return switch (material) {
            case WRITABLE_BOOK, WRITTEN_BOOK -> new BookItemLootMeta(section);
            case ENCHANTED_BOOK -> new EnchantmentStorageItemLootMeta(section);
            case FIREWORK_STAR -> new FireworkEffectItemLootMeta(section);
            case FIREWORK_ROCKET -> new FireworkItemLootMeta(section);
            case KNOWLEDGE_BOOK -> new KnowledgeBookItemLootMeta(section);
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> new LeatherArmorItemLootMeta(section);
            case POTION, SPLASH_POTION, LINGERING_POTION, TIPPED_ARROW -> new PotionItemLootMeta(section);
            case PLAYER_HEAD -> new SkullItemLootMeta(section);
            case SUSPICIOUS_STEW -> new SuspiciousStewItemLootMeta(section);
            case TROPICAL_FISH_BUCKET -> new TropicalFishBucketItemLootMeta(section);
            case AXOLOTL_BUCKET -> new AxolotlBucketItemLootMeta(section);
            case BUNDLE -> new BundleItemLootMeta(section);
            case MAP -> new MapItemLootMeta(section);
            case OMINOUS_BOTTLE -> new OminousBottleItemLootMeta(section);
            default -> new ItemLootMeta(section);
        };
    }

    @SuppressWarnings("deprecation")
    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        Material material = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (itemMeta.hasDisplayName()) stringBuilder.append("display-name: '").append(LootUtils.decolorize(itemMeta.getDisplayName())).append('\'').append('\n');
        if (itemMeta.hasCustomModelData()) stringBuilder.append("custom-model-data: ").append(itemMeta.getCustomModelData()).append('\n');
        if (itemMeta.isUnbreakable()) stringBuilder.append("unbreakable: true\n");

        if (itemMeta instanceof Repairable repairable && repairable.hasRepairCost())
            stringBuilder.append("repair-cost: ").append(repairable.getRepairCost()).append('\n');

        if (itemMeta instanceof Damageable damageable && damageable.hasDamage())
            stringBuilder.append("durability: ").append(itemStack.getType().getMaxDurability() - damageable.getDamage()).append('\n');

        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            stringBuilder.append("lore:\n");
            for (String line : itemMeta.getLore())
                stringBuilder.append("  - '").append(LootUtils.decolorize(line)).append("'\n");
        }

        Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
        if (itemFlags.size() == ItemFlag.values().length) {
            stringBuilder.append("hide-flags: true\n");
        } else if (!itemFlags.isEmpty()) {
            stringBuilder.append("hide-flags:\n");
            for (ItemFlag itemFlag : itemFlags)
                stringBuilder.append("  - '").append(itemFlag.name().toLowerCase()).append("'\n");
        }

        if (!itemMeta.getEnchants().isEmpty() && material != Material.ENCHANTED_BOOK) {
            stringBuilder.append("enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemMeta.getEnchants().entrySet())
                stringBuilder.append("  ").append(entry.getKey().getKey().getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        Multimap<Attribute, AttributeModifier> attributes = itemMeta.getAttributeModifiers();
        if (attributes != null) {
            stringBuilder.append("attributes:\n");
            int i = 0;
            for (Attribute attribute : attributes.keySet()) {
                for (AttributeModifier modifier : attributes.get(attribute)) {
                    stringBuilder.append("  ").append(i++).append(":\n");
                    stringBuilder.append("    ").append("name: ").append('\'').append(attribute.getKey().getKey()).append("'\n");
                    stringBuilder.append("    ").append("amount: ").append(modifier.getAmount()).append('\n');
                    stringBuilder.append("    ").append("operation: ").append(modifier.getOperation().name().toLowerCase()).append("'\n");
                    if (modifier.getSlot() != null)
                        stringBuilder.append("    ").append("slot: ").append(modifier.getSlot().name().toLowerCase()).append("'\n");
                }
            }
        }

        if (Tag.ITEMS_BANNERS.isTagged(material)) {
            BannerItemLootMeta.applyProperties(itemStack, stringBuilder);
            return;
        }

        if (NMSUtil.getVersionNumber() >= 20 && Tag.ITEMS_TRIMMABLE_ARMOR.isTagged(material)) {
            switch (material) {
                case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> ColorableArmorItemLootMeta.applyProperties(itemStack, stringBuilder);
                default -> ArmorItemLootMeta.applyProperties(itemStack, stringBuilder);
            }
            return;
        }

        switch (material) {
            case WRITABLE_BOOK, WRITTEN_BOOK -> BookItemLootMeta.applyProperties(itemStack, stringBuilder);
            case ENCHANTED_BOOK -> EnchantmentStorageItemLootMeta.applyProperties(itemStack, stringBuilder);
            case FIREWORK_STAR -> FireworkEffectItemLootMeta.applyProperties(itemStack, stringBuilder);
            case FIREWORK_ROCKET -> FireworkItemLootMeta.applyProperties(itemStack, stringBuilder);
            case KNOWLEDGE_BOOK -> KnowledgeBookItemLootMeta.applyProperties(itemStack, stringBuilder);
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> LeatherArmorItemLootMeta.applyProperties(itemStack, stringBuilder);
            case POTION, SPLASH_POTION, LINGERING_POTION, TIPPED_ARROW -> PotionItemLootMeta.applyProperties(itemStack, stringBuilder);
            case PLAYER_HEAD -> SkullItemLootMeta.applyProperties(itemStack, stringBuilder);
            case SUSPICIOUS_STEW -> SuspiciousStewItemLootMeta.applyProperties(itemStack, stringBuilder);
            case TROPICAL_FISH_BUCKET -> TropicalFishBucketItemLootMeta.applyProperties(itemStack, stringBuilder);
            case AXOLOTL_BUCKET -> AxolotlBucketItemLootMeta.applyProperties(itemStack, stringBuilder);
            case BUNDLE -> BundleItemLootMeta.applyProperties(itemStack, stringBuilder);
            case MAP -> MapItemLootMeta.applyProperties(itemStack, stringBuilder);
            case OMINOUS_BOTTLE -> OminousBottleItemLootMeta.applyProperties(itemStack, stringBuilder);
        }
    }

    private record AttributeData(Attribute attribute, NumberProvider amount, AttributeModifier.Operation operation, EquipmentSlot slot) {

        @SuppressWarnings("removal") // using correct API per version
        public AttributeModifier toAttributeModifier(LootContext context) {
            if (NMSUtil.getVersionNumber() >= 21) {
                return new AttributeModifier(this.attribute.getKey(), this.amount.getDouble(context), this.operation, this.slot.getGroup());
            } else {
                return new AttributeModifier(UUID.randomUUID(), this.attribute.getKey().getKey(), this.amount.getDouble(context), this.operation, this.slot);
            }
        }

    }

    protected record EnchantmentData(Enchantment enchantment, NumberProvider level) { }

}
