package dev.rosewood.roseloot.hook.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class CoinsEngineEconomyProvider implements EconomyProvider {

    private final boolean enabled;

    public CoinsEngineEconomyProvider() {
        this.enabled = Bukkit.getPluginManager().getPlugin("CoinsEngine") != null;
    }

    @Override
    public String formatCurrency(double amount, String currencyId) {
        if (!this.enabled)
            return String.valueOf(amount);

        Currency currency = CoinsEngineAPI.getCurrency(currencyId);
        if (currency != null)
            return currency.format(amount);

        return String.valueOf(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer, String currencyId) {
        if (!this.enabled)
            return 0;

        Currency currency = CoinsEngineAPI.getCurrency(currencyId);
        if (currency != null) {
            Player player = offlinePlayer.getPlayer();
            if (player != null)
                return CoinsEngineAPI.getBalance(offlinePlayer.getPlayer(), currency);

            CoinsUser coinsUser = CoinsEngineAPI.getUserData(offlinePlayer.getUniqueId());
            if (coinsUser != null)
                return coinsUser.getBalance(currency);
        }

        return 0;
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount, String currencyId) {
        if (!this.enabled)
            return;

        Currency currency = CoinsEngineAPI.getCurrency(currencyId);
        if (currency != null) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                CoinsEngineAPI.addBalance(player, currency, amount);
                return;
            }

            CoinsUser coinsUser = CoinsEngineAPI.getUserData(offlinePlayer.getUniqueId());
            if (coinsUser != null)
                coinsUser.addBalance(currency, amount);
        }
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount, String currencyId) {
        if (!this.enabled)
            return;

        Currency currency = CoinsEngineAPI.getCurrency(currencyId);
        if (currency != null) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                CoinsEngineAPI.removeBalance(player, currency, amount);
                return;
            }

            CoinsUser coinsUser = CoinsEngineAPI.getUserData(offlinePlayer.getUniqueId());
            if (coinsUser != null)
                coinsUser.removeBalance(currency, amount);
        }
    }

}
