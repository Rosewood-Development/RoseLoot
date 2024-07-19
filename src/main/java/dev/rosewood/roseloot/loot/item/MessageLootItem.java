package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class MessageLootItem implements GroupTriggerableLootItem<MessageLootItem> {

    private final MessageType messageType;
    private final String message;
    private final NumberProvider fadeIn, duration, fadeOut;
    private final boolean broadcast;
    private final boolean combineDuplicateMessages;

    protected MessageLootItem(MessageType messageType, String message, NumberProvider fadeIn, NumberProvider duration, NumberProvider fadeOut, boolean broadcast, boolean combineDuplicateMessages) {
        this.messageType = messageType;
        this.message = message;
        this.fadeIn = fadeIn;
        this.duration = duration;
        this.fadeOut = fadeOut;
        this.broadcast = broadcast;
        this.combineDuplicateMessages = combineDuplicateMessages;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Consumer<Player> consumer = player -> {
            switch (this.messageType) {
                case CHAT_RAW -> player.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(context.applyPlaceholders(this.message)));
                case CHAT -> player.sendMessage(context.formatText(this.message));
                case HOTBAR -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(context.formatText(this.message)));
                case TITLE -> player.sendTitle(context.formatText(this.message), null, this.fadeIn.getInteger(context), this.duration.getInteger(context), this.fadeOut.getInteger(context));
                case SUBTITLE -> player.sendTitle(null, context.formatText(this.message), this.fadeIn.getInteger(context), this.duration.getInteger(context), this.fadeOut.getInteger(context));
            }
        };

        if (this.broadcast) {
            for (Player player : Bukkit.getOnlinePlayers())
                consumer.accept(player);
        } else {
            context.getLootingPlayer().ifPresent(consumer);
        }
    }

    @Override
    public void trigger(LootContext context, Location location, List<MessageLootItem> others) {
        this.trigger(context, location); // Simply excludes others, only send this message once
    }

    @Override
    public boolean canTriggerWith(MessageLootItem other) {
        return this.combineDuplicateMessages
                && this.messageType == other.messageType
                && this.message.equals(other.message)
                && this.broadcast == other.broadcast;
    }

    public static MessageLootItem fromSection(ConfigurationSection section) {
        MessageType messageType = MessageType.fromString(section.getString("message-type"));
        if (messageType == null)
            return null;

        String message = section.getString("value");
        NumberProvider fadeIn = NumberProvider.fromSection(section, "fade-in", 20);
        NumberProvider duration = NumberProvider.fromSection(section, "duration", 20);
        NumberProvider fadeOut = NumberProvider.fromSection(section, "fade-out", 20);
        boolean broadcast = section.getBoolean("broadcast", false);
        boolean combineDuplicateMessages = section.getBoolean("combine-duplicate-messages", true);

        return new MessageLootItem(messageType, message, fadeIn, duration, fadeOut, broadcast, combineDuplicateMessages);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MessageLootItem that = (MessageLootItem) o;
        return this.broadcast == that.broadcast
                && this.combineDuplicateMessages == that.combineDuplicateMessages
                && this.messageType == that.messageType
                && Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.messageType, this.message, this.broadcast, this.combineDuplicateMessages);
    }

}
