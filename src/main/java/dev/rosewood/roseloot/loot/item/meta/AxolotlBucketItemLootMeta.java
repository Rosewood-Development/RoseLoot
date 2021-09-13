package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;

public class AxolotlBucketItemLootMeta extends ItemLootMeta {

    private boolean copyLooted;
    private Axolotl.Variant variant;

    public AxolotlBucketItemLootMeta(ConfigurationSection section) {
        super(section);

        if (section.isBoolean("copy-looted")) this.copyLooted = section.getBoolean("copy-looted");

        String variantString = section.getString("variant");
        if (variantString != null) {
            for (Axolotl.Variant value : Axolotl.Variant.values()) {
                if (value.name().equalsIgnoreCase(variantString)) {
                    this.variant = value;
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        AxolotlBucketMeta itemMeta = (AxolotlBucketMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.copyLooted && context.getLootedEntity() instanceof Axolotl) {
            itemMeta.setVariant(((Axolotl) context.getLootedEntity()).getVariant());
        } else if (this.variant != null) {
            itemMeta.setVariant(this.variant);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        AxolotlBucketMeta itemMeta = (AxolotlBucketMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (itemMeta.hasVariant()) stringBuilder.append("variant: ").append(itemMeta.getVariant().name().toLowerCase()).append('\n');
    }

}
