package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.manager.CooldownManager;
import dev.rosewood.roseloot.util.TimeUtils;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * cooldown:custom_id,player,15m
 * value 1: Custom ID of the cooldown
 * value 2: <player> or <global>
 * value 3: Time of the cooldown with a time suffix (ms, s, m, h, d)
 */
public class CooldownCondition extends BaseLootCondition {

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
        if (values.length != 2 && values.length != 3)
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

        if (values.length == 3) {
            String stringDuration = values[2];
            this.cooldownLength = TimeUtils.getDuration(stringDuration);
        } else {
            this.cooldownLength = 0;
        }

        return true;
    }

}
