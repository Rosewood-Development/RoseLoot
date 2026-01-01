package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.OminousBottleMeta;

public class OminousBottleItemLootMeta extends ItemLootMeta {

    private final NumberProvider amplifier;

    public OminousBottleItemLootMeta(ConfigurationSection section) {
        super(section);

        this.amplifier = NumberProvider.fromSection(section, "amplifier", null);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        if (!(itemStack.getItemMeta() instanceof OminousBottleMeta itemMeta))
            return itemStack;

        if (this.amplifier != null) itemMeta.setAmplifier(this.amplifier.getInteger(context));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!(itemStack.getItemMeta() instanceof OminousBottleMeta itemMeta))
            return;

        stringBuilder.append("amplifier: ").append(itemMeta.getAmplifier()).append('\n');
    }

}
