package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.StringProvider;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;

public class MusicInstrumentItemLootMeta extends ItemLootMeta {

    private final StringProvider musicInstrument;

    public MusicInstrumentItemLootMeta(ConfigurationSection section) {
        super(section);

        this.musicInstrument = StringProvider.fromSection(section, "music-instrument", null);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        itemStack = super.apply(itemStack, context);

        MusicInstrumentMeta itemMeta = (MusicInstrumentMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.musicInstrument != null) {
            MusicInstrument musicInstrument = null;
            NamespacedKey namespacedKey = NamespacedKey.fromString(this.musicInstrument.get(context));
            if (namespacedKey != null) {
                if (NMSUtil.getVersionNumber() >= 21) {
                    musicInstrument = Registry.INSTRUMENT.get(namespacedKey);
                } else {
                    musicInstrument = MusicInstrument.getByKey(namespacedKey);
                }
            }

            if (musicInstrument != null)
                itemMeta.setInstrument(musicInstrument);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        MusicInstrumentMeta itemMeta = (MusicInstrumentMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        MusicInstrument instrument = itemMeta.getInstrument();
        if (instrument != null) stringBuilder.append("music-instrument: ").append(instrument.getKey().getKey()).append('\n');
    }

}
