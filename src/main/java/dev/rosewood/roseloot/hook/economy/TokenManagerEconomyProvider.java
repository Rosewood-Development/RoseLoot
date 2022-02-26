package dev.rosewood.roseloot.hook.economy;

import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.api.TokenManager;
import me.realized.tokenmanager.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class TokenManagerEconomyProvider implements EconomyProvider {

    private TokenManager economy;

    public TokenManagerEconomyProvider() {
        if (Bukkit.getPluginManager().isPluginEnabled("TokenManager"))
            this.economy = TokenManagerPlugin.getInstance();
    }

    @Override
    public String formatCurrency(double amount) {
        if (this.economy == null)
            return String.valueOf(amount);
        return NumberUtil.withCommas((long) amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (this.economy == null || !offlinePlayer.isOnline())
            return 0;
        return this.economy.getTokens(offlinePlayer.getPlayer()).orElse(0);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.economy == null || !offlinePlayer.isOnline())
            return;
        this.economy.addTokens(offlinePlayer.getPlayer(), (long) amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.economy == null || !offlinePlayer.isOnline())
            return;
        this.economy.removeTokens(offlinePlayer.getPlayer(), (long) amount);
    }

}
