package dev.rosewood.roseloot.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyProvider {

    String formatCurrency(double amount);

    double checkBalance(OfflinePlayer offlinePlayer);

    void deposit(OfflinePlayer offlinePlayer, double amount);

    void withdraw(OfflinePlayer offlinePlayer, double amount);

}
