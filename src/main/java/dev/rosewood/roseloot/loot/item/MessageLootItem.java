package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.Optional;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class MessageLootItem implements TriggerableLootItem {

    private final MessageType messageType;
    private final String message;
    private final NumberProvider fadeIn, duration, fadeOut;

    public MessageLootItem(MessageType messageType, String message, NumberProvider fadeIn, NumberProvider duration, NumberProvider fadeOut) {
        this.messageType = messageType;
        this.message = message;
        this.fadeIn = fadeIn;
        this.duration = duration;
        this.fadeOut = fadeOut;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Player> lootingPlayer = context.getLootingPlayer();
        if (lootingPlayer.isEmpty())
            return;

        Player player = lootingPlayer.get();
        switch (this.messageType) {
            case CHAT_RAW -> player.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(context.applyPlaceholders(this.message)));
            case CHAT -> player.sendMessage(context.formatText(this.message));
            case HOTBAR -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(context.formatText(this.message)));
            case TITLE -> player.sendTitle(context.formatText(this.message), null, this.fadeIn.getInteger(), this.duration.getInteger(), this.fadeOut.getInteger());
            case SUBTITLE -> player.sendTitle(null, context.formatText(this.message), this.fadeIn.getInteger(), this.duration.getInteger(), this.fadeOut.getInteger());
        }
    }

    public static MessageLootItem fromSection(ConfigurationSection section) {
        MessageType messageType = MessageType.fromString(section.getString("message-type"));
        if (messageType == null)
            return null;

        String message = section.getString("value");
        NumberProvider fadeIn = NumberProvider.fromSection(section, "fade-in", 20);
        NumberProvider duration = NumberProvider.fromSection(section, "duration", 20);
        NumberProvider fadeOut = NumberProvider.fromSection(section, "fade-out", 20);

        return new MessageLootItem(messageType, message, fadeIn, duration, fadeOut);
    }

    public enum MessageType {
        CHAT_RAW,
        CHAT,
        HOTBAR,
        TITLE,
        SUBTITLE;

        public static MessageType fromString(String name) {
            for (MessageType value : values())
                if (value.name().equalsIgnoreCase(name))
                    return value;
            return null;
        }
    }

}
