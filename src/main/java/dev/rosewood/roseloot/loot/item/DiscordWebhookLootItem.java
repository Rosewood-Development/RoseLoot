package dev.rosewood.roseloot.loot.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class DiscordWebhookLootItem implements TriggerableLootItem {

    private final String url, content, avatarUrl, username;

    public DiscordWebhookLootItem(String url, String content, String avatarUrl, String username) {
        this.url = url;
        this.content = content;
        this.avatarUrl = avatarUrl;
        this.username = username;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        if (this.url == null || this.content == null)
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");

        Bukkit.getScheduler().runTaskAsynchronously(RoseLoot.getInstance(), () -> {
            try {
                JsonObject json = new JsonObject();
                json.add("content", new JsonPrimitive(context.applyPlaceholders(this.content)));
                if (this.avatarUrl != null)
                    json.add("avatar_url", new JsonPrimitive(this.avatarUrl));
                if (this.username != null)
                    json.add("username", new JsonPrimitive(context.applyPlaceholders(this.username)));

                HttpsURLConnection connection = (HttpsURLConnection) new URL(this.url).openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("User-Agent", "RoseLoot");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                try (OutputStream stream = connection.getOutputStream()) {
                    stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    stream.flush();
                }

                connection.getInputStream().close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static DiscordWebhookLootItem fromSection(ConfigurationSection section) {
        String url = section.getString("url");
        String content = section.getString("content");
        String avatarUrl = section.getString("avatar-url");
        String username = section.getString("username");
        return new DiscordWebhookLootItem(url, content, avatarUrl, username);
    }

}
