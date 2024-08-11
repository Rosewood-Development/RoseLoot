package dev.rosewood.roseloot.loot.item.meta.component;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.StringProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;

public class JukeboxPlayableComponentLootMeta implements ComponentLootMeta {

    private final StringProvider songKey;
    private Boolean showInTooltip;

    public JukeboxPlayableComponentLootMeta(ConfigurationSection section) {
        this.songKey = StringProvider.fromSection(section, "song", null);
        if (section.isBoolean("show-in-tooltip")) this.showInTooltip = section.getBoolean("show-in-tooltip");
    }

    @Override
    public void apply(ItemMeta itemMeta, LootContext context) {
        JukeboxPlayableComponent jukeboxPlayableComponent = itemMeta.getJukeboxPlayable();

        if (this.songKey != null) {
            String songKeyString = this.songKey.get(context);
            NamespacedKey songKey = NamespacedKey.fromString(songKeyString);
            if (songKey != null) {
                jukeboxPlayableComponent.setSongKey(songKey);
            } else {
                RoseLoot.getInstance().getLogger().warning("Invalid jukebox-playable-component song: " + songKeyString);
            }
        }
        if (this.showInTooltip != null) jukeboxPlayableComponent.setShowInTooltip(this.showInTooltip);

        itemMeta.setJukeboxPlayable(jukeboxPlayableComponent);
    }

    public static void applyProperties(ItemMeta itemMeta, StringBuilder stringBuilder) {
        if (!itemMeta.hasJukeboxPlayable())
            return;

        JukeboxPlayableComponent jukeboxPlayableComponent = itemMeta.getJukeboxPlayable();

        stringBuilder.append("jukebox-playable-component:\n");
        stringBuilder.append("  song: ").append(jukeboxPlayableComponent.getSongKey()).append('\n');
        stringBuilder.append("  show-in-tooltip: ").append(jukeboxPlayableComponent.isShowInTooltip()).append('\n');
    }

}
