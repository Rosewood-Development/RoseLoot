package dev.rosewood.roseloot.loot.item.meta;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.component.LootItemComponentProvider;
import dev.rosewood.roseloot.nms.NMSAdapter;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.BlockInfo;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
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
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public class ItemLootMeta {

    private static class MaterialMappings {
        private static final Map<Material, Function<ConfigurationSection, ? extends ItemLootMeta>> CONSTRUCTORS;
        private static final Map<Material, BiConsumer<ItemStack, StringBuilder>> PROPERTY_APPLIERS;
        static {
            CONSTRUCTORS = new HashMap<>();
            PROPERTY_APPLIERS = new HashMap<>();

            if (NMSUtil.getVersionNumber() > 21 || (NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3))
                mapMaterials(BundleItemLootMeta::new, BundleItemLootMeta::applyProperties, Tag.ITEMS_BUNDLES.getValues().toArray(Material[]::new));

            mapMaterials(BookItemLootMeta::new, BookItemLootMeta::applyProperties, Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);
            mapMaterials(EnchantmentStorageItemLootMeta::new, EnchantmentStorageItemLootMeta::applyProperties, Material.ENCHANTED_BOOK);
            mapMaterials(FireworkEffectItemLootMeta::new, FireworkEffectItemLootMeta::applyProperties, Material.FIREWORK_STAR);
            mapMaterials(FireworkItemLootMeta::new, FireworkItemLootMeta::applyProperties, Material.FIREWORK_ROCKET);
            mapMaterials(KnowledgeBookItemLootMeta::new, KnowledgeBookItemLootMeta::applyProperties, Material.KNOWLEDGE_BOOK);
            mapMaterials(PotionItemLootMeta::new, PotionItemLootMeta::applyProperties, Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION, Material.TIPPED_ARROW);
            mapMaterials(SkullItemLootMeta::new, SkullItemLootMeta::applyProperties, Material.PLAYER_HEAD);
            mapMaterials(SuspiciousStewItemLootMeta::new, SuspiciousStewItemLootMeta::applyProperties, Material.SUSPICIOUS_STEW);
            mapMaterials(TropicalFishBucketItemLootMeta::new, TropicalFishBucketItemLootMeta::applyProperties, Material.TROPICAL_FISH_BUCKET);
            mapMaterials(MapItemLootMeta::new, MapItemLootMeta::applyProperties, Material.MAP);

            mapMaterials(BannerItemLootMeta::new, BannerItemLootMeta::applyProperties, Tag.ITEMS_BANNERS.getValues().toArray(Material[]::new));

            if (NMSUtil.getVersionNumber() >= 17) {
                mapMaterials(AxolotlBucketItemLootMeta::new, AxolotlBucketItemLootMeta::applyProperties, Material.AXOLOTL_BUCKET);
                mapMaterials(BundleItemLootMeta::new, BundleItemLootMeta::applyProperties, Material.BUNDLE);
            }

            if (NMSUtil.getVersionNumber() >= 19)
                mapMaterials(MusicInstrumentItemLootMeta::new, MusicInstrumentItemLootMeta::applyProperties, Material.GOAT_HORN);

            Material[] leatherArmor = { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS };
            if (NMSUtil.getVersionNumber() >= 20) {
                mapMaterials(ArmorItemLootMeta::new, ArmorItemLootMeta::applyProperties, Tag.ITEMS_TRIMMABLE_ARMOR.getValues().toArray(Material[]::new));
                mapMaterials(ColorableArmorItemLootMeta::new, ColorableArmorItemLootMeta::applyProperties, leatherArmor); // overwrites the above
            } else {
                mapMaterials(LeatherArmorItemLootMeta::new, LeatherArmorItemLootMeta::applyProperties, ObjectArrays.concat(leatherArmor, Material.LEATHER_HORSE_ARMOR));
            }

            if (NMSUtil.getVersionNumber() >= 21)
                mapMaterials(OminousBottleItemLootMeta::new, OminousBottleItemLootMeta::applyProperties, Material.OMINOUS_BOTTLE);
        }

        private static void mapMaterials(Function<ConfigurationSection, ? extends ItemLootMeta> constructor, BiConsumer<ItemStack, StringBuilder> propertyApplier, Material... materials) {
            for (Material material : materials) {
                CONSTRUCTORS.put(material, constructor);
                PROPERTY_APPLIERS.put(material, propertyApplier);
            }
        }
    }

    private static class ComponentMappings {
        private static final Map<String, Function<ConfigurationSection, ? extends LootItemComponent>> CONSTRUCTORS;
        private static final Map<String, BiConsumer<ItemStack, StringBuilder>> PROPERTY_APPLIERS;
        static {
            CONSTRUCTORS = new HashMap<>();
            PROPERTY_APPLIERS = new HashMap<>();

            if (NMSUtil.isPaper()) {
                try {
                    String name = null;
                    int major = NMSUtil.getVersionNumber();
                    int minor = NMSUtil.getMinorVersionNumber();
                    if (major == 21 && minor == 4) {
                        name = "v1_21_3";
                    } else if (major == 21 && minor == 5) {
                        name = "v1_21_4";
                    }

                    if (name != null) {
                        LootItemComponentProvider provider = (LootItemComponentProvider) Class.forName("dev.rosewood.roseloot.loot.item.component." + name + ".LootItemComponentProviderImpl").getConstructor().newInstance();
                        CONSTRUCTORS.putAll(provider.provideLootItemComponentConstructors());
                        PROPERTY_APPLIERS.putAll(provider.provideLootItemComponentPropertyApplicators());

                        if (!CONSTRUCTORS.keySet().equals(PROPERTY_APPLIERS.keySet()))
                            throw new IllegalStateException("Mismatch between LootItemComponentProvider values: " + name);

                        RoseLoot.getInstance().getLogger().info("Loaded " + CONSTRUCTORS.size() + " loot item components");
                    }
                } catch (Exception ignored) { }
            }
        }
    }

    private final StringProvider displayName;
    private final StringProvider lore;
    private final NumberProvider customModelData;
    private Boolean unbreakable;
    private final NumberProvider repairCost;
    private final NumberProvider durability;
    private NumberProvider enchantmentLevel;
    private boolean includeTreasureEnchantments;
    private List<ItemFlag> hideFlags;
    protected List<Enchantment> randomEnchantments;
    protected NumberProvider randomEnchantmentsAmount;
    protected List<EnchantmentData> enchantments;
    private List<AttributeData> attributes;
    private final boolean copyBlockState;
    private final boolean copyBlockData;
    private final boolean copyBlockName;
    private final StringProvider lootTable;
    protected boolean restoreVanillaAttributes;
    private final boolean looterPickupOnly;
    protected List<LootItemComponent> components;

    public ItemLootMeta(ConfigurationSection section) {
        this.displayName = StringProvider.fromSection(section, "display-name", null);
        this.lore = StringProvider.fromSection(section, "lore", null);
        this.customModelData = NumberProvider.fromSection(section, "custom-model-data", null);
        if (section.isBoolean("unbreakable")) this.unbreakable = section.getBoolean("unbreakable");
        this.repairCost = NumberProvider.fromSection(section, "repair-cost", null);
        this.durability = NumberProvider.fromSection(section, "durability", null);

        ConfigurationSection enchantRandomlySection = section.getConfigurationSection("enchant-randomly");
        if (enchantRandomlySection != null) {
            this.enchantmentLevel = NumberProvider.fromSection(enchantRandomlySection, "level", 30);
            this.includeTreasureEnchantments = enchantRandomlySection.getBoolean("treasure", false);
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
                Enchantment enchantment = VersionUtils.getEnchantment(enchantmentName);
                if (enchantment != null)
                    this.randomEnchantments.add(enchantment);
            }
        }

        this.randomEnchantmentsAmount = NumberProvider.fromSection(section, "random-enchantments-amount", 1);

        ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            List<EnchantmentData> enchantments = new ArrayList<>();
            for (String enchantmentName : enchantmentsSection.getKeys(false)) {
                Enchantment enchantment = VersionUtils.getEnchantment(enchantmentName);
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

                Attribute attribute = VersionUtils.getAttribute(name);
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

        this.copyBlockState = section.getBoolean("copy-block-state", false);
        this.copyBlockData = section.getBoolean("copy-block-data", false);
        this.copyBlockName = section.getBoolean("copy-block-name", false);
        this.lootTable = StringProvider.fromSection(section, "loot-table", null);

        this.components = new ArrayList<>();
        ConfigurationSection componentsSection = section.getConfigurationSection("components");
        if (componentsSection != null) {
            for (var entry : ComponentMappings.CONSTRUCTORS.entrySet()) {
                String key = entry.getKey();
                if (componentsSection.contains(key))
                    this.components.add(entry.getValue().apply(componentsSection));
            }
        }

        this.restoreVanillaAttributes = section.getBoolean("restore-vanilla-attributes", true);
        this.looterPickupOnly = section.getBoolean("looter-pickup-only", false);
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
        if (this.enchantmentLevel != null) {
            Optional<World> world = context.get(LootContextParams.ORIGIN).map(Location::getWorld);
            itemStack = NMSAdapter.getHandler().enchantWithLevels(itemStack, this.enchantmentLevel.getInteger(context), this.includeTreasureEnchantments, world.orElse(null));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.displayName != null) itemMeta.setDisplayName(this.displayName.getFormatted(context));
        if (this.lore != null) itemMeta.setLore(this.lore.getListFormatted(context));
        if (this.customModelData != null) itemMeta.setCustomModelData(this.customModelData.getInteger(context));
        if (this.unbreakable != null) itemMeta.setUnbreakable(this.unbreakable);
        if (this.hideFlags != null) itemMeta.addItemFlags(this.hideFlags.toArray(new ItemFlag[0]));

        Material type = itemStack.getType();
        if (type != Material.ENCHANTED_BOOK) {
            if (this.randomEnchantments != null) {
                List<Enchantment> enchantmentsSource;
                if (!this.randomEnchantments.isEmpty()) {
                    // Not empty, use the suggested
                    enchantmentsSource = this.randomEnchantments;
                } else {
                    // Empty, pick from every applicable enchantment for the item
                    enchantmentsSource = Arrays.asList(VersionUtils.getEnchantments());
                }

                // Filter out enchantments that can't go on the item
                List<Enchantment> possibleEnchantments = new ArrayList<>();
                for (Enchantment enchantment : enchantmentsSource)
                    if (enchantment.canEnchantItem(itemStack) && enchantment.isDiscoverable())
                        possibleEnchantments.add(enchantment);

                // Apply the number of enchantments desired
                int amount = this.randomEnchantmentsAmount.getInteger(context);
                for (int i = 0; i < amount; i++) {
                    // Filter out enchantments that conflict with enchantments already on the item
                    possibleEnchantments = possibleEnchantments.stream().filter(Predicate.not(itemMeta::hasConflictingEnchant)).toList();
                    if (possibleEnchantments.isEmpty())
                        break;

                    Enchantment enchantment = possibleEnchantments.get(LootUtils.RANDOM.nextInt(possibleEnchantments.size()));
                    int level = LootUtils.RANDOM.nextInt(enchantment.getMaxLevel()) + 1;
                    itemMeta.addEnchant(enchantment, level, false);
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
            if (NMSUtil.getVersionNumber() >= 21 && NMSUtil.isPaper() && this.restoreVanillaAttributes && type.isItem())
                attributes.putAll(type.getDefaultAttributeModifiers());
            itemMeta.setAttributeModifiers(attributes);
        }

        if (itemMeta instanceof Damageable damageable && this.durability != null) {
            int max = type.getMaxDurability();
            int durabilityValue;
            if (this.durability.isPercentage()) {
                durabilityValue = (int) Math.round(this.durability.getDouble(context) * max);
            } else {
                durabilityValue = this.durability.getInteger(context);
            }
            damageable.setDamage(max - Math.max(0, Math.min(durabilityValue, max)));
        }

        if (this.repairCost != null && itemMeta instanceof Repairable repairable)
            repairable.setRepairCost(this.repairCost.getInteger(context));

        Optional<BlockInfo> lootedBlock = context.getLootedBlockInfo();
        if (lootedBlock.isPresent() && lootedBlock.get().getMaterial() == type) {
            BlockInfo block = lootedBlock.get();
            if (this.copyBlockState && itemMeta instanceof BlockStateMeta blockStateMeta)
                blockStateMeta.setBlockState(block.getState());

            if (this.copyBlockData && itemMeta instanceof BlockDataMeta blockDataMeta)
                blockDataMeta.setBlockData(block.getData());

            if (this.copyBlockName && block.getState() instanceof Nameable nameable)
                itemMeta.setDisplayName(nameable.getCustomName());
        }

        if (this.lootTable != null && itemMeta instanceof BlockStateMeta blockStateMeta && blockStateMeta.getBlockState() instanceof Lootable lootable) {
            String lootTableKeyString = this.lootTable.get(context);
            NamespacedKey lootTableKey = NamespacedKey.fromString(lootTableKeyString);
            if (lootTableKey != null) {
                LootTable lootTable = Bukkit.getLootTable(lootTableKey);
                if (lootTable != null) {
                    lootable.setLootTable(lootTable);
                    blockStateMeta.setBlockState((BlockState) lootable);
                } else {
                    RoseLoot.getInstance().getLogger().warning("Could not set loot-table on item, server loot table not found: " + lootTableKey);
                }
            } else {
                RoseLoot.getInstance().getLogger().warning("Could not set loot-table on item, invalid loot table key: " + lootTableKeyString);
            }
        }

        if (this.looterPickupOnly)
            context.getLootingPlayer().ifPresent(player -> LootUtils.setRestrictedItemPickup(itemMeta, player.getUniqueId()));

        itemStack.setItemMeta(itemMeta);

        ItemStack componentItemStack = itemStack;
        this.components.forEach(x -> {
            try {
                x.apply(componentItemStack, context);
            } catch (Exception e) {
                e.printStackTrace(); // Log exception but continue
            }
        });

        return componentItemStack;
    }

    public static ItemLootMeta fromSection(Material material, ConfigurationSection section) {
        return MaterialMappings.CONSTRUCTORS.getOrDefault(material, ItemLootMeta::new).apply(section);
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

        MaterialMappings.PROPERTY_APPLIERS.getOrDefault(material, (x, y) -> {}).accept(itemStack, stringBuilder);
    }

    public static void applyComponentProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        StringBuilder componentBuilder = new StringBuilder();
        for (BiConsumer<ItemStack, StringBuilder> propertyApplier : ComponentMappings.PROPERTY_APPLIERS.values())
            propertyApplier.accept(itemStack, componentBuilder);

        if (!componentBuilder.isEmpty()) {
            stringBuilder.append("components:\n");
            String componentsString = componentBuilder.toString().indent(2);
            stringBuilder.append(componentsString);
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
