package dev.rosewood.roseloot.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyProvider implements EconomyProvider {

    private Economy economy;

    public VaultEconomyProvider() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp != null)
                this.economy = rsp.getProvider();
        }
    }

    @Override
    public String formatCurrency(double amount) {
        if (this.economy == null)
            return String.valueOf(amount);
        return this.economy.format(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (this.economy == null)
            return -1;
        return this.economy.getBalance(offlinePlayer);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.economy != null)
            this.economy.depositPlayer(offlinePlayer, amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.economy != null)
            this.economy.withdrawPlayer(offlinePlayer, amount);
    }

}
