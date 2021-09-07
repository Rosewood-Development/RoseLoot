package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.economy.EconomyProvider;
import dev.rosewood.roseloot.economy.PlayerPointsEconomyProvider;
import dev.rosewood.roseloot.economy.VaultEconomyProvider;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class EconomyLootItem implements TriggerableLootItem<Double> {

    private final EconomyPlugin plugin;
    private double min;
    private double max;

    public EconomyLootItem(EconomyPlugin plugin, int min, int max) {
        this.plugin = plugin;
        this.min = min;
        this.max = max;
    }

    @Override
    public Double create(LootContext context) {
        return LootUtils.randomInRange(this.min, this.max);
    }

    @Override
    public boolean combineWith(LootItem<?> lootItem) {
        if (!(lootItem instanceof EconomyLootItem))
            return false;

        EconomyLootItem other = (EconomyLootItem) lootItem;
        if (this.plugin != other.plugin)
            return false;

        this.min += other.min;
        this.max += other.max;
        return true;
    }

    @Override
    public void trigger(LootContext context, Player player, Location location) {
        if (player != null)
            this.plugin.deposit(player, LootUtils.randomInRange(this.min, this.max));
    }

    public static EconomyLootItem fromSection(ConfigurationSection section) {
        EconomyPlugin economyPlugin = EconomyPlugin.fromString(section.getString("economy"));
        if (economyPlugin == null)
            return null;

        int minExp, maxExp;
        if (section.contains("amount")) {
            minExp = maxExp = section.getInt("amount");
        } else {
            minExp = section.getInt("min", 1);
            maxExp = section.getInt("max", 1);
        }

        return new EconomyLootItem(economyPlugin, minExp, maxExp);
    }

    public enum EconomyPlugin implements EconomyProvider {
        VAULT(new VaultEconomyProvider()),
        PLAYERPOINTS(new PlayerPointsEconomyProvider());

        private final EconomyProvider economyProvider;

        EconomyPlugin(EconomyProvider economyProvider) {
            this.economyProvider = economyProvider;
        }

        @Override
        public String formatCurrency(double amount) {
            return this.economyProvider.formatCurrency(amount);
        }

        @Override
        public double checkBalance(OfflinePlayer offlinePlayer) {
            return this.economyProvider.checkBalance(offlinePlayer);
        }

        @Override
        public void deposit(OfflinePlayer offlinePlayer, double amount) {
            this.economyProvider.deposit(offlinePlayer, amount);
        }

        @Override
        public void withdraw(OfflinePlayer offlinePlayer, double amount) {
            this.economyProvider.withdraw(offlinePlayer, amount);
        }

        public static EconomyPlugin fromString(String name) {
            for (EconomyPlugin value : values())
                if (value.name().equalsIgnoreCase(name))
                    return value;
            return null;
        }
    }

}
