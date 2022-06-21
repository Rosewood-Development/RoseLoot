package dev.rosewood.roseloot.hook.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.black_ixx.playerpoints.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerPointsEconomyProvider implements EconomyProvider {

    private final boolean enabled;
    private PlayerPointsAPI economy;

    public PlayerPointsEconomyProvider() {
        this.enabled = Bukkit.getPluginManager().getPlugin("PlayerPoints") != null;
        if (this.enabled)
            this.economy = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public String formatCurrency(double amount) {
        if (!this.enabled)
            return String.valueOf(amount);
        return PlayerPoints.getInstance().getManager(LocaleManager.class).getCurrencyName((int) Math.round(amount));
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (!this.enabled)
            return 0;
        return this.economy.look(offlinePlayer.getUniqueId());
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.enabled)
            this.economy.give(offlinePlayer.getUniqueId(), (int) Math.round(amount));
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.enabled)
            this.economy.take(offlinePlayer.getUniqueId(), (int) Math.round(amount));
    }

}
