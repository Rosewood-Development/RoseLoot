package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class NoteBlockSoundComponent implements LootItemComponent {

    private final StringProvider sound;

    public NoteBlockSoundComponent(ConfigurationSection section) {
        this.sound = StringProvider.fromSection(section, "note-block-sound", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.sound != null) {
            String soundKey = this.sound.get(context);
            itemStack.setData(DataComponentTypes.NOTE_BLOCK_SOUND, Key.key(soundKey.toLowerCase()));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.NOTE_BLOCK_SOUND))
            return;

        Key sound = itemStack.getData(DataComponentTypes.NOTE_BLOCK_SOUND);
        stringBuilder.append("note-block-sound: ").append(sound.asMinimalString()).append('\n');
    }

} 
