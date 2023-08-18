package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.manager.CooldownManager;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

/**
 * cooldown:custom_id,player,15m
 * value 1: Custom ID of the cooldown
 * value 2: <player> or <global>
 * value 3: Time of the cooldown with a time suffix (ms, s, m, h, d)
 */
public class CooldownCondition extends BaseLootCondition {

    private static final Pattern ENTIRE_DURATION_PATTERN = Pattern.compile("((\\d+)(ms|[smhd]))+");
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)(ms|[smhd])");

    private String cooldownId;
    private boolean playerBased;
    private long cooldownLength;

    public CooldownCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Player> lootingPlayer = context.getLootingPlayer();
        if (this.playerBased && lootingPlayer.isEmpty())
            return false;

        UUID target = this.playerBased ? lootingPlayer.get().getUniqueId() : null;
        return RoseLoot.getInstance().getManager(CooldownManager.class).checkCooldown(this.cooldownId, target, this.cooldownLength);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 3)
            return false;

        this.cooldownId = values[0];

        String type = values[1];
        if (type.equalsIgnoreCase("player")) {
            this.playerBased = true;
        } else if (type.equalsIgnoreCase("global")) {
            this.playerBased = false;
        } else {
            return false;
        }

        String stringDuration = values[2];
        if (!ENTIRE_DURATION_PATTERN.matcher(stringDuration).matches())
            return false;

        long duration = 0;
        Matcher matcher = DURATION_PATTERN.matcher(stringDuration);
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String suffix = matcher.group(2);
            switch (suffix.toLowerCase()) {
                case "ms" -> duration += value;
                case "s" -> duration += TimeUnit.SECONDS.toMillis(value);
                case "m" -> duration += TimeUnit.MINUTES.toMillis(value);
                case "h" -> duration += TimeUnit.HOURS.toMillis(value);
                case "d" -> duration += TimeUnit.DAYS.toMillis(value);
                default -> {
                    return false;
                }
            }
        }

        if (duration <= 0)
            return false;

        this.cooldownLength = duration;
        return true;
    }

}
