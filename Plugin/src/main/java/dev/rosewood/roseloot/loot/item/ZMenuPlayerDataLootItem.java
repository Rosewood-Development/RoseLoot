package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ZMenuPlayerDataLootItem implements AutoTriggerableLootItem {

    private final String variable;
    private final boolean set;
    private final NumberProvider amount;
    private final StringProvider value;
    private final NumberProvider expirationSeconds;

    protected ZMenuPlayerDataLootItem(String variable, boolean set, NumberProvider amount, StringProvider value, NumberProvider expirationSeconds) {
        this.variable = variable;
        this.set = set;
        this.amount = amount;
        this.value = value;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Player> optionalPlayer = context.getLootingPlayer();
        if (optionalPlayer.isEmpty())
            return;

        var provider = Bukkit.getServer().getServicesManager().getRegistration(DataManager.class);
        if (provider == null) {
            RoseLoot.getInstance().getLogger().warning("Couldn't get zMenu DataManager from services manager");
            return;
        }

        DataManager dataManager = provider.getProvider();
        PlayerData playerData = dataManager.getOrCreate(optionalPlayer.get().getUniqueId());

        long expiration = this.expirationSeconds.getInteger(context);
        if (expiration > 0) {
            expiration = System.currentTimeMillis() + expiration * 1000;
        } else {
            expiration = 0;
        }

        Optional<Data> optionalData = playerData.getData(this.variable);
        if (optionalData.isPresent() && !this.set) {
            optionalData.get().add(this.amount.getInteger(context));
        } else {
            Object value = this.value == null ? this.amount.getDouble(context) : this.value.get(context);
            playerData.addData(new RoseData(this.variable, value, expiration));
        }
    }

    public static ZMenuPlayerDataLootItem fromSection(ConfigurationSection section) {
        String variable = section.getString("variable");
        boolean set = section.getBoolean("set", false);
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        StringProvider value = StringProvider.fromSection(section, "value", null);
        NumberProvider expiration = NumberProvider.fromSection(section, "expiration", 0);
        return new ZMenuPlayerDataLootItem(variable, set, amount, value, expiration);
    }

    /**
     * Copy/pasted from the zMenu source since they don't seem to expose an implementing object in the API artifact
     */
    private static class RoseData implements Data {

        private final String key;
        private final long expiredAt;
        private Object value;

        public RoseData(String key, Object value, long expiredAt) {
            super();
            this.key = key;
            this.value = value;
            this.expiredAt = expiredAt;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public long getExpiredAt() {
            return this.expiredAt;
        }

        @Override
        public boolean isExpired() {
            return this.expiredAt != 0 && System.currentTimeMillis() > this.expiredAt;
        }

        @Override
        public void add(int amount) {
            int value = this.safeStringToInt(this.value.toString());
            this.value = value + amount;
        }

        @Override
        public void remove(int amount) {
            int value = this.safeStringToInt(this.value.toString());
            this.value = value - amount;
        }

        @Override
        public void negate() {
            this.value = -Integer.parseInt(this.value.toString());
        }

        private int safeStringToInt(String str) {
            if (str.contains(".")) {
                double doubleValue = Double.parseDouble(str);
                return (int) doubleValue;
            } else {
                return Integer.parseInt(str);
            }
        }

    }

}
