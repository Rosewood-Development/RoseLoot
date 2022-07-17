package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.hook.economy.EconomyProvider;
import dev.rosewood.roseloot.hook.economy.PlayerPointsEconomyProvider;
import dev.rosewood.roseloot.hook.economy.TokenManagerEconomyProvider;
import dev.rosewood.roseloot.hook.economy.TreasuryEconomyProvider;
import dev.rosewood.roseloot.hook.economy.VaultEconomyProvider;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

public class EconomyLootItem implements TriggerableLootItem {

    private final EconomyPlugin plugin;
    private final List<NumberProvider> amounts;

    public EconomyLootItem(EconomyPlugin plugin, NumberProvider amounts) {
        this.plugin = plugin;
        this.amounts = new ArrayList<>(List.of(amounts));
    }

    @Override
    public boolean combineWith(LootItem lootItem) {
        if (!(lootItem instanceof EconomyLootItem other) || this.plugin != other.plugin)
            return false;

        this.amounts.addAll(other.amounts);
        return true;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        double amount = this.amounts.stream().mapToDouble(NumberProvider::getDouble).sum();
        context.getPlaceholders().add("economy_amount", amount);
        context.getLootingPlayer().ifPresent(x -> this.plugin.deposit(x, amount));
    }

    public static EconomyLootItem fromSection(ConfigurationSection section) {
        EconomyPlugin economyPlugin = EconomyPlugin.fromString(section.getString("economy"));
        if (economyPlugin == null)
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        return new EconomyLootItem(economyPlugin, amount);
    }

    public enum EconomyPlugin implements EconomyProvider {
        VAULT(new VaultEconomyProvider()),
        TREASURY(new TreasuryEconomyProvider()),
        PLAYERPOINTS(new PlayerPointsEconomyProvider()),
        TOKENMANAGER(new TokenManagerEconomyProvider());

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
