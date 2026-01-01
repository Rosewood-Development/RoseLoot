package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Optional;
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
        itemStack = super.apply(itemStack, context);

        if (!(itemStack.getItemMeta() instanceof AxolotlBucketMeta itemMeta))
            return itemStack;

        Optional<Axolotl> lootedEntity = context.getAs(LootContextParams.LOOTED_ENTITY, Axolotl.class);
        if (this.copyLooted && lootedEntity.isPresent()) {
            itemMeta.setVariant(lootedEntity.get().getVariant());
        } else if (this.variant != null) {
            itemMeta.setVariant(this.variant);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof AxolotlBucketMeta itemMeta))
            return;

        if (itemMeta.hasVariant()) stringBuilder.append("variant: ").append(itemMeta.getVariant().name().toLowerCase()).append('\n');
    }

}
