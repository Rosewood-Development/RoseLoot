package dev.rosewood.roseloot.loot.item.meta;

import com.google.common.collect.ObjectArrays;
import dev.rosewood.rosegarden.utils.NMSUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MaterialMappings {

    public static final Map<Material, Function<ConfigurationSection, ? extends ItemLootMeta>> CONSTRUCTORS;
    public static final Map<Material, BiConsumer<ItemStack, StringBuilder>> PROPERTY_APPLIERS;

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
