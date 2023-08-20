package dev.rosewood.roseloot.manager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.roseloot.util.TimeUtils;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class CooldownManager extends Manager {

    private final Multimap<String, Cooldown> cooldowns;

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
        this.cooldowns.values().removeIf(Cooldown::isExpired);

        Cooldown cooldown = this.cooldowns.get(cooldownId).stream()
                .filter(x -> Objects.equals(x.player(), player))
                .findFirst()
                .orElse(null);

        if (cooldown != null)
            return false;

        this.cooldowns.put(cooldownId, new Cooldown(cooldownId, player, System.currentTimeMillis() + expiration));
        return true;
    }

    /**
     * Gets all active cooldowns for a player, including global cooldowns
     *
     * @param player The player to get cooldowns for
     * @return All active cooldowns for the player
     */
    public Collection<Cooldown> getActiveCooldowns(UUID player) {
        return this.cooldowns.values().stream()
                .filter(x -> x.player() == null || x.player().equals(player))
                .filter(x -> !x.isExpired())
                .toList();
    }

    /**
     * Resets all cooldowns for a player
     *
     * @param player The player to reset cooldowns for
     */
    public void resetCooldowns(UUID player) {
        this.cooldowns.values().removeIf(x -> Objects.equals(x.player(), player));
    }

    @Override
    public void reload() {
        this.rosePlugin.getManager(DataManager.class).getCooldowns().forEach(x -> this.cooldowns.put(x.id(), x));
    }

    @Override
    public void disable() {
        this.rosePlugin.getManager(DataManager.class).setCooldowns(this.cooldowns.values());
        this.cooldowns.clear();
    }

    public record Cooldown(String id, UUID player, long expiration) {

        public boolean isExpired() {
            return this.expiration < System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return TimeUtils.getTimeStringUntil(this.expiration);
        }

    }

}
