package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.rosestacker.utils.ItemUtils;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class RoseStackerLootItem extends ItemLootItem {

    private final StringProvider stackItemType;
    private final NumberProvider stackSize;

    protected RoseStackerLootItem(StringProvider stackItemType, NumberProvider stackSize, ItemLootItem base) {
        super(base);
        this.stackItemType = stackItemType;
        this.stackSize = stackSize;
        this.resolveItem(LootContext.none());
    }

    @Override
    protected Optional<ItemStack> resolveItem(LootContext context) {
        String stackItemTypeString = this.stackItemType.get(context);
        String itemType = this.item.get(context);
        int stackSize = this.stackSize.getInteger(context);

        StackItemType stackItemType = StackItemType.fromString(stackItemTypeString);
        if (stackItemType == null) {
            this.logFailToResolveMessage(stackItemTypeString);
            return Optional.empty();
        }

        ItemStack itemStack = stackItemType.get(itemType, stackSize);
        if (itemStack == null) {
            this.logFailToResolveMessage(itemType);
            return Optional.empty();
        }

        return Optional.of(itemStack);
    }

    @Override
    protected String getFailToResolveMessage(String tagId) {
        return "Failed to resolve RoseStacker type [" + tagId + "]";
    }

    public static RoseStackerLootItem fromSection(ConfigurationSection section) {
        ItemLootItem base = ItemLootItem.fromSection(section, "stack-type", false);
        if (base == null)
            return null;

        StringProvider stackType = StringProvider.fromSection(section, "item", null);
        if (stackType == null)
            return null;

        NumberProvider stackSize = NumberProvider.fromSection(section, "stack-size", 1);
        return new RoseStackerLootItem(stackType, stackSize, base);
    }

    private enum StackItemType {
        BLOCK {
            @Override
            public ItemStack get(String type, int stackSize) {
                Material material = Material.matchMaterial(type);
                if (material == null)
                    return null;
                return ItemUtils.getBlockAsStackedItemStack(material, stackSize);
            }
        },
        SPAWN_EGG {
            @Override
            public ItemStack get(String type, int stackSize) {
                EntityType entityType = EntityType.fromName(type);
                if (entityType == null)
                    return null;
                return ItemUtils.getEntityAsStackedItemStack(entityType, stackSize);
            }
        },
        SPAWNER {
            @Override
            public ItemStack get(String type, int stackSize) {
                EntityType entityType = EntityType.fromName(type);
                if (entityType == null)
                    return null;
                return ItemUtils.getSpawnerAsStackedItemStack(entityType, stackSize);
            }
        };

        public abstract ItemStack get(String type, int stackSize);

        public static StackItemType fromString(String name) {
            for (StackItemType type : values())
                if (type.name().equalsIgnoreCase(name))
                    return type;
            return null;
        }
    }

}
