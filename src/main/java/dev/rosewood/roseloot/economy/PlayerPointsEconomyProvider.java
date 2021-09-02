package dev.rosewood.roseloot.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerPointsEconomyProvider implements EconomyProvider {

    private PlayerPointsAPI economy;

    public PlayerPointsEconomyProvider() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints"))
            this.economy = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public String formatCurrency(double amount) {
        if (this.economy == null)
            return String.valueOf(amount);
        return PlayerPoints.getInstance().getManager(LocaleManager.class).getCurrencyName((int) Math.round(amount));
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (this.economy == null)
            return -1;
        return this.economy.look(offlinePlayer.getUniqueId());
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.economy != null)
            this.economy.give(offlinePlayer.getUniqueId(), (int) Math.round(amount));
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.economy != null)
            this.economy.take(offlinePlayer.getUniqueId(), (int) Math.round(amount));
    }

}
