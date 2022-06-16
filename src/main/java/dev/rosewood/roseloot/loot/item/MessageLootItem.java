package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.TriConsumer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MessageLootItem implements TriggerableLootItem<MessageLootItem.StoredChatMessage> {

    private final StoredChatMessage storedChatMessage;

    public MessageLootItem(MessageType messageType, String message, Integer fadeIn, Integer duration, Integer fadeOut) {
        this.storedChatMessage = new StoredChatMessage(messageType, message, fadeIn, duration, fadeOut);
    }

    @Override
    public StoredChatMessage create(LootContext context) {
        return this.storedChatMessage;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        context.getLootingPlayer().ifPresent(x -> this.storedChatMessage.invoke(context, x));
    }

    public static MessageLootItem fromSection(ConfigurationSection section) {
        MessageType messageType = MessageType.fromString(section.getString("message-type"));
        if (messageType == null)
            return null;

        String message = section.getString("value");
        int fadeIn = section.getInt("fade-in", 20);
        int duration = section.getInt("duration", 20);
        int fadeOut = section.getInt("fade-out", 20);

        return new MessageLootItem(messageType, message, fadeIn, duration, fadeOut);
    }

    public record StoredChatMessage(MessageType messageType, String text, int fadeIn, int duration, int fadeOut) {

        /**
         * Sends the chat message to a player
         *
         * @param context the loot context
         * @param player  the player to send the message to
         */
        public void invoke(LootContext context, Player player) {
            this.messageType.invoke(context, player, this);
        }

    }

    @SuppressWarnings("deprecation")
    public enum MessageType {
        CHAT_RAW((context, player, message) -> player.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(context.applyPlaceholders(message.text())))),
        CHAT((context, player, message) -> player.sendMessage(context.formatText(message.text()))),
        HOTBAR((context, player, message) -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(context.formatText(message.text())))),
        TITLE((context, player, message) -> player.sendTitle(context.formatText(message.text()), null, message.fadeIn(), message.duration(), message.fadeOut())),
        SUBTITLE((context, player, message) -> player.sendTitle(null, context.formatText(message.text()), message.fadeIn(), message.duration(), message.fadeOut()));

        private final TriConsumer<LootContext, Player, StoredChatMessage> consumer;

        MessageType(TriConsumer<LootContext, Player, StoredChatMessage> consumer) {
            this.consumer = consumer;
        }

        public void invoke(LootContext context, Player player, StoredChatMessage message) {
            this.consumer.accept(context, player, message);
        }

        public static MessageType fromString(String name) {
            for (MessageType value : values())
                if (value.name().equalsIgnoreCase(name))
                    return value;
            return null;
        }
    }

}
