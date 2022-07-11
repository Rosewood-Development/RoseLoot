package dev.rosewood.roseloot.manager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.Objects;
import java.util.UUID;

public class CooldownManager extends Manager {

    private final Multimap<String, CooldownEntry> cooldowns;

    public CooldownManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.cooldowns = ArrayListMultimap.create();
    }

    /**
     * Checks if a player is on cooldown for a certain cooldown ID.
     *
     * @param cooldownId The ID of the cooldown
     * @param player The player to check, null for global
     * @param expiration The expiration time of the cooldown
     * @return true if a new cooldown was started, false if one already existed
     */
    public boolean checkCooldown(String cooldownId, UUID player, long expiration) {
        // Remove any expired cooldowns
        this.cooldowns.values().removeIf(CooldownEntry::isExpired);

        CooldownEntry cooldownEntry = this.cooldowns.get(cooldownId).stream().filter(x -> Objects.equals(x.player(), player)).findFirst().orElse(null);
        if (cooldownEntry != null)
            return false;

        this.cooldowns.put(cooldownId, new CooldownEntry(player, System.currentTimeMillis() + expiration));
        return true;
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public record CooldownEntry(UUID player, long expiration) {

        public boolean isExpired() {
            return this.expiration() < System.currentTimeMillis();
        }

    }

}
