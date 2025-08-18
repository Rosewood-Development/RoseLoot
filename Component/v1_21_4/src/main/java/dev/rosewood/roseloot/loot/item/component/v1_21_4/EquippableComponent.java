package dev.rosewood.roseloot.loot.item.component.v1_21_4;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

class EquippableComponent implements LootItemComponent {

    private final EquipmentSlot slot;
    private final StringProvider equipSound;
    private final StringProvider assetId;
    private final StringProvider cameraOverlay;
    private final StringProvider allowedEntities;
    private final boolean dispensable;
    private final boolean swappable;
    private final boolean damageOnHurt;
    private final boolean equipOnInteract;

    public EquippableComponent(ConfigurationSection section) {
        ConfigurationSection equippableSection = section.getConfigurationSection("equippable");
        if (equippableSection != null) {
            this.slot = EquipmentSlot.valueOf(equippableSection.getString("slot", "HAND").toUpperCase());
            this.equipSound = StringProvider.fromSection(equippableSection, "equip-sound", null);
            this.assetId = StringProvider.fromSection(equippableSection, "asset-id", null);
            this.cameraOverlay = StringProvider.fromSection(equippableSection, "camera-overlay", null);
            this.allowedEntities = StringProvider.fromSection(equippableSection, "allowed-entities", null);
            this.dispensable = equippableSection.getBoolean("dispensable", false);
            this.swappable = equippableSection.getBoolean("swappable", true);
            this.damageOnHurt = equippableSection.getBoolean("damage-on-hurt", false);
            this.equipOnInteract = equippableSection.getBoolean("equip-on-interact", false);
        } else {
            this.slot = EquipmentSlot.HAND;
            this.equipSound = null;
            this.assetId = null;
            this.cameraOverlay = null;
            this.allowedEntities = null;
            this.dispensable = false;
            this.swappable = true;
            this.damageOnHurt = false;
            this.equipOnInteract = false;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        Equippable.Builder builder = Equippable.equippable(this.slot);
        
        if (this.equipSound != null)
            builder.equipSound(Key.key(this.equipSound.get(context).toLowerCase()));

        if (this.assetId != null)
            builder.assetId(Key.key(this.assetId.get(context).toLowerCase()));
        
        if (this.cameraOverlay != null)
            builder.cameraOverlay(Key.key(this.cameraOverlay.get(context).toLowerCase()));
        
        if (this.allowedEntities != null) {
            List<String> entityStrings = this.allowedEntities.getList(context);
            Registry<EntityType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE);
            if (entityStrings.size() == 1 && entityStrings.getFirst().startsWith("#")) {
                String tag = entityStrings.getFirst().toLowerCase();
                TagKey<EntityType> tagKey = TagKey.create(RegistryKey.ENTITY_TYPE, Key.key(tag.substring(1)));
                builder.allowedEntities(registry.getTag(tagKey));
            } else {
                List<EntityType> entityTypes = new ArrayList<>();
                for (String value : entityStrings) {
                    if (value.startsWith("#")) {
                        TagKey<EntityType> tagKey = TagKey.create(RegistryKey.ENTITY_TYPE, Key.key(value.substring(1)));
                        Tag<EntityType> tag = registry.getTag(tagKey);
                        entityTypes.addAll(tag.resolve(registry));
                    } else {
                        Key key = Key.key(value.toLowerCase());
                        EntityType entityType = registry.get(key);
                        if (entityType != null)
                            entityTypes.add(entityType);
                    }
                }
                builder.allowedEntities(RegistrySet.keySetFromValues(RegistryKey.ENTITY_TYPE, entityTypes));
            }
        }

        builder.dispensable(this.dispensable)
                .swappable(this.swappable)
                .damageOnHurt(this.damageOnHurt)
                .equipOnInteract(this.equipOnInteract);
        
        itemStack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.EQUIPPABLE))
            return;

        Equippable equippable = itemStack.getData(DataComponentTypes.EQUIPPABLE);
        stringBuilder.append("equippable:\n");
        stringBuilder.append("  slot: ").append(equippable.slot().name().toLowerCase()).append('\n');
        stringBuilder.append("  equip-sound: ").append(equippable.equipSound().asMinimalString()).append('\n');
        
        if (equippable.assetId() != null) {
            stringBuilder.append("  asset-id: ").append(equippable.assetId().asMinimalString()).append('\n');
        }
        
        if (equippable.cameraOverlay() != null) {
            stringBuilder.append("  camera-overlay: ").append(equippable.cameraOverlay().asString()).append('\n');
        }
        
        if (equippable.allowedEntities() != null) {
            if (equippable.allowedEntities() instanceof Tag<?> tag) {
                stringBuilder.append("  allowed-entities: '#").append(tag.tagKey().key().asMinimalString()).append("'\n");
            } else {
                stringBuilder.append("  allowed-entities:\n");
                for (TypedKey<EntityType> key : equippable.allowedEntities().values())
                    stringBuilder.append("    - '").append(key.asMinimalString()).append("'\n");
            }
        }
        
        stringBuilder.append("  dispensable: ").append(equippable.dispensable()).append('\n');
        stringBuilder.append("  swappable: ").append(equippable.swappable()).append('\n');
        stringBuilder.append("  damage-on-hurt: ").append(equippable.damageOnHurt()).append('\n');
        stringBuilder.append("  equip-on-interact: ").append(equippable.equipOnInteract()).append('\n');
    }

} 
