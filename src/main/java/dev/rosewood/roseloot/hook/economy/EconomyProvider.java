package dev.rosewood.roseloot.hook.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyProvider {

    /**
     * Formats a currency value to a user-displayable string.
     *
     * @param amount The currency amount to format
     * @return The formatted currency string
     */
    String formatCurrency(double amount);

    /**
     * Gets the balance of the specified player, or 0 if the balance was unable to be looked up.
     *
     * @param offlinePlayer The player to get the balance of
     * @return The balance of the player
     */
    double checkBalance(OfflinePlayer offlinePlayer);

    /**
     * Deposits the specified amount of currency into the specified player's account.
     *
     * @param offlinePlayer The player to deposit the currency into
     * @param amount The amount of currency to deposit
     */
    void deposit(OfflinePlayer offlinePlayer, double amount);

    /**
     * Withdraws the specified amount of currency from the specified player's account.
     *
     * @param offlinePlayer The player to withdraw the currency from
     * @param amount The amount of currency to withdraw
     */
    void withdraw(OfflinePlayer offlinePlayer, double amount);

}
