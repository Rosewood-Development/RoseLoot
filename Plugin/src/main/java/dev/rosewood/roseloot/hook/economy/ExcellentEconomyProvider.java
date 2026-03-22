package dev.rosewood.roseloot.hook.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;

public class ExcellentEconomyProvider implements EconomyProvider {

    private boolean enabled;
    private ExcellentEconomyAPI api;

    public ExcellentEconomyProvider() {
        this.enabled = Bukkit.getPluginManager().getPlugin("CoinsEngine") != null || Bukkit.getPluginManager().getPlugin("ExcellentEconomy") != null;
        if (this.enabled) {
            RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
            if (provider != null) {
                this.api = provider.getProvider();
            } else {
                this.enabled = false;
            }
        }
    }

    @Override
    public String formatCurrency(double amount, String currencyId) {
        if (!this.enabled)
            return String.valueOf(amount);

        ExcellentCurrency currency = this.api.getCurrency(currencyId);
        if (currency != null)
            return currency.format(amount);

        return String.valueOf(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer, String currencyId) {
        if (!this.enabled)
            return 0;

        ExcellentCurrency currency = this.api.getCurrency(currencyId);
        if (currency != null) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) // Currently only supports online player lookups to avoid a blocking database call
                return this.api.getBalance(offlinePlayer.getPlayer(), currency);
        }

        return 0;
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount, String currencyId) {
        if (!this.enabled)
            return;

        ExcellentCurrency currency = this.api.getCurrency(currencyId);
        if (currency != null) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                this.api.deposit(player, currency, amount);
                return;
            }

            this.api.depositAsync(offlinePlayer.getUniqueId(), currency, amount);
        }
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount, String currencyId) {
        if (!this.enabled)
            return;

        ExcellentCurrency currency = this.api.getCurrency(currencyId);
        if (currency != null) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                this.api.withdraw(player, currency, amount);
                return;
            }

            this.api.withdrawAsync(offlinePlayer.getUniqueId(), currency, amount);
        }
    }

}
