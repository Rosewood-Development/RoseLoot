package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.SoundLootItem.SoundInstance;
import dev.rosewood.roseloot.util.NumberProvider;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundLootItem implements TriggerableLootItem<SoundInstance> {

    private final SoundInstance soundInstance;

    private SoundLootItem(SoundInstance soundInstance) {
        this.soundInstance = soundInstance;
    }

    @Override
    public SoundInstance create(LootContext context) {
        return this.soundInstance;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        this.create(context).trigger(context.getLootingPlayer(), location);
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

        float volume = (float) NumberProvider.fromSection(section, "volume", 1).getDouble();
        float pitch = (float) NumberProvider.fromSection(section, "pitch", 1).getDouble();
        boolean playerOnly = section.getBoolean("player-only", true);
        SoundInstance soundInstance = new SoundInstance(sound, category, volume, pitch, playerOnly);
        return new SoundLootItem(soundInstance);
    }

    public static class SoundInstance {

        private final String sound;
        private final SoundCategory category;
        private final float volume, pitch;
        private final boolean playerOnly;

        public SoundInstance(String sound, SoundCategory category, float volume, float pitch, boolean playerOnly) {
            this.sound = sound;
            this.category = category;
            this.volume = volume;
            this.pitch = pitch;
            this.playerOnly = playerOnly;
        }

        /**
         * Triggers the stored sound
         *
         * @param player The Player to trigger the sound for if playerOnly is true
         * @param location The Location to trigger the sound at
         */
        public void trigger(Player player, Location location) {
            if (this.playerOnly) {
                if (player != null)
                    player.playSound(location, this.sound, this.category, this.volume, this.pitch);
            } else {
                World world = location.getWorld();
                if (world != null)
                    world.playSound(location, this.sound, this.category, this.volume, this.pitch);
            }
        }

    }

}
