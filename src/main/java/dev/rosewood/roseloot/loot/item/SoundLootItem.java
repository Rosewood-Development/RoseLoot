package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundLootItem implements TriggerableLootItem {

    private final String sound;
    private final SoundCategory category;
    private final NumberProvider volume;
    private final NumberProvider pitch;
    private final boolean playerOnly;

    private SoundLootItem(String sound, SoundCategory category, NumberProvider volume, NumberProvider pitch, boolean playerOnly) {
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.playerOnly = playerOnly;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Player> player = context.getLootingPlayer();
        if (this.playerOnly) {
            player.ifPresent(value -> value.playSound(location, this.sound, this.category, (float) this.volume.getDouble(context), (float) this.pitch.getDouble(context)));
        } else {
            World world = location.getWorld();
            if (world != null)
                world.playSound(location, this.sound, this.category, (float) this.volume.getDouble(context), (float) this.pitch.getDouble(context));
        }
    }

    public static SoundLootItem fromSection(ConfigurationSection section) {
        String sound = section.getString("sound");
        if (sound == null)
            return null;

        String categoryName = section.getString("category");
        SoundCategory category = null;
        if (categoryName != null) {
            for (SoundCategory value : SoundCategory.values()) {
                if (value.name().equalsIgnoreCase(categoryName)) {
                    category = value;
                    break;
                }
            }
        }

        if (category == null)
            category = SoundCategory.MASTER;

        NumberProvider volume = NumberProvider.fromSection(section, "volume", 1);
        NumberProvider pitch = NumberProvider.fromSection(section, "pitch", 1);
        boolean playerOnly = section.getBoolean("player-only", true);
        return new SoundLootItem(sound, category, volume, pitch, playerOnly);
    }

}
