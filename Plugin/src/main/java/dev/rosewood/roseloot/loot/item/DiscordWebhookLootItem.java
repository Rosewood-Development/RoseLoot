package dev.rosewood.roseloot.loot.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.StringProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class DiscordWebhookLootItem implements TriggerableLootItem {

    private final StringProvider url;
    private final StringProvider content;
    private final StringProvider avatarUrl;
    private final StringProvider username;

    protected DiscordWebhookLootItem(StringProvider url, StringProvider content, StringProvider avatarUrl, StringProvider username) {
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
                json.add("content", new JsonPrimitive(this.content.get(context)));
                if (this.avatarUrl != null)
                    json.add("avatar_url", new JsonPrimitive(this.avatarUrl.get(context)));
                if (this.username != null)
                    json.add("username", new JsonPrimitive(this.username.get(context)));

                HttpsURLConnection connection = (HttpsURLConnection) new URL(this.url.get(context)).openConnection();
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
        StringProvider url = StringProvider.fromSection(section, "url", null);
        StringProvider content = StringProvider.fromSection(section, "content", null);
        StringProvider avatarUrl = StringProvider.fromSection(section, "avatar-url", null);
        StringProvider username = StringProvider.fromSection(section, "username", null);
        return new DiscordWebhookLootItem(url, content, avatarUrl, username);
    }

}
