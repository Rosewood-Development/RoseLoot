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

    public static class StoredChatMessage {

        private final MessageType messageType;
        private final String message;
        private final int fadeIn, duration, fadeOut;

        public StoredChatMessage(MessageType messageType, String message, int fadeIn, int duration, int fadeOut) {
            this.messageType = messageType;
            this.message = message;
            this.fadeIn = fadeIn;
            this.duration = duration;
            this.fadeOut = fadeOut;
        }

        /**
         * @return the message type to send
         */
        public MessageType getMessageType() {
            return this.messageType;
        }

        /**
         * @return the message that is being sent
         */
        public String getText() {
            return this.message;
        }

        /**
         * @return the fade in time in ticks
         */
        public int getFadeIn() {
            return this.fadeIn;
        }

        /**
         * @return the duration of the message in ticks
         */
        public int getDuration() {
            return this.duration;
        }

        /**
         * @return the fade out time in ticks
         */
        public int getFadeOut() {
            return this.fadeOut;
        }

        /**
         * Sends the chat message to a player
         *
         * @param context the loot context
         * @param player the player to send the message to
         */
        public void invoke(LootContext context, Player player) {
            this.messageType.invoke(context, player, this);
        }

    }

    public enum MessageType {
        CHAT_RAW((context, player, message) -> player.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(context.applyPlaceholders(message.getText())))),
        CHAT((context, player, message) -> player.sendMessage(context.formatText(message.getText()))),
        HOTBAR((context, player, message) -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(context.formatText(message.getText())))),
        TITLE((context, player, message) -> player.sendTitle(context.formatText(message.getText()), null, message.getFadeIn(), message.getDuration(), message.getFadeOut())),
        SUBTITLE((context, player, message) -> player.sendTitle(null, context.formatText(message.getText()), message.getFadeIn(), message.getDuration(), message.getFadeOut()));

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
