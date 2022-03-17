package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmorItemLootMeta extends ItemLootMeta {

    private Color color;

    public LeatherArmorItemLootMeta(ConfigurationSection section) {
        super(section);

        String colorString = section.getString("color");
        if (colorString != null) {
            try {
                java.awt.Color awtColor = java.awt.Color.decode(colorString);
                this.color = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
            } catch (NumberFormatException ignored) { }
        }
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.color != null) itemMeta.setColor(this.color);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        Color color = itemMeta.getColor();
        stringBuilder.append("color: '").append(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).append("'\n");
    }

}
