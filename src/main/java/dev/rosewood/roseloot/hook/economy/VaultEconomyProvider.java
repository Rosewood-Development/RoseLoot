package dev.rosewood.roseloot.hook.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyProvider implements EconomyProvider {

    private final boolean enabled;
    private Economy economy;

    public VaultEconomyProvider() {
        this.enabled = Bukkit.getPluginManager().getPlugin("Vault") != null;
        if (this.enabled) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp != null)
                this.economy = rsp.getProvider();
        }
    }

    @Override
    public String formatCurrency(double amount) {
        if (!this.enabled)
            return String.valueOf(amount);
        return this.economy.format(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (!this.enabled)
            return 0;
        return this.economy.getBalance(offlinePlayer);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.enabled)
            this.economy.depositPlayer(offlinePlayer, amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.enabled)
            this.economy.withdrawPlayer(offlinePlayer, amount);
    }

}
