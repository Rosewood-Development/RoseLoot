package dev.rosewood.roseloot.hook.economy;

import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class TokenManagerEconomyProvider implements EconomyProvider {

    private final boolean enabled;

    public TokenManagerEconomyProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("TokenManager");
    }

    @Override
    public String formatCurrency(double amount) {
        if (!this.enabled)
            return String.valueOf(amount);
        return NumberUtil.withCommas((long) amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (!this.enabled)
            return 0;
        return TokenManagerPlugin.getInstance().getTokens(offlinePlayer.getPlayer()).orElse(0);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.enabled)
            TokenManagerPlugin.getInstance().addTokens(offlinePlayer.getPlayer(), (long) amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.enabled)
            TokenManagerPlugin.getInstance().removeTokens(offlinePlayer.getPlayer(), (long) amount);
    }

}
