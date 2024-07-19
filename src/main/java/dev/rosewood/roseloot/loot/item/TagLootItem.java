package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class TagLootItem extends ItemLootItem {

    protected TagLootItem(ItemLootItem base) {
        super(base);
        this.resolveItem(LootContext.none());
    }

    @Override
    protected Optional<ItemStack> resolveItem(LootContext context) {
        String tagString = this.item.get(context);
        NamespacedKey namespacedKey = NamespacedKey.fromString(tagString);
        if (namespacedKey == null) {
            this.logFailToResolveMessage(tagString);
            return Optional.empty();
        }

        // Look for matching tags
        Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, namespacedKey, Material.class);
        if (tag == null)
            tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, namespacedKey, Material.class);

        if (tag == null) {
            this.logFailToResolveMessage(tagString);
            return Optional.empty();
        }

        List<Material> values = new ArrayList<>(tag.getValues());
        Material material = values.get(LootUtils.RANDOM.nextInt(values.size()));
        return Optional.of(new ItemStack(material));
    }

    @Override
    protected String getFailToResolveMessage(String tagId) {
        return "Failed to resolve tag [" + tagId + "]";
    }

    public static TagLootItem fromSection(ConfigurationSection section) {
        ItemLootItem base = ItemLootItem.fromSection(section, "tag");
        if (base == null)
            return null;

        return new TagLootItem(base);
    }

}
