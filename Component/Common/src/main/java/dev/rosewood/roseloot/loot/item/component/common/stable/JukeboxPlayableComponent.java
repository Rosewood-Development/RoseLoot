package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class JukeboxPlayableComponent implements LootItemComponent {

    private final StringProvider song;

    public JukeboxPlayableComponent(ConfigurationSection section) {
        this.song = StringProvider.fromSection(section, "jukebox-playable", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.song != null) {
            String songKey = this.song.get(context);
            if (songKey != null) {
                Registry<JukeboxSong> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG);
                JukeboxSong jukeboxSong = registry.get(Key.key(songKey.toLowerCase()));
                if (jukeboxSong != null) {
                    JukeboxPlayable.Builder builder = JukeboxPlayable.jukeboxPlayable(jukeboxSong);
                    itemStack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, builder.build());
                }
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.JUKEBOX_PLAYABLE))
            return;

        JukeboxPlayable jukeboxPlayable = itemStack.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
        NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).getKey(jukeboxPlayable.jukeboxSong());
        if (key != null)
            stringBuilder.append("  jukebox-playable: '").append(key.asMinimalString()).append("'\n");
    }

} 
