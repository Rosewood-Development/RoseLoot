package dev.rosewood.roseloot.hook.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyProvider {

    /**
     * Formats a currency value to a user-displayable string.
     *
     * @param amount The currency amount to format
     * @param currency The currency ID to use, nullable
     * @return The formatted currency string
     */
    String formatCurrency(double amount, String currency);

    /**
     * Gets the balance of the specified player, or 0 if the balance was unable to be looked up.
     *
     * @param offlinePlayer The player to get the balance of
     * @param currency The currency ID to use, nullable
     * @return The balance of the player
     */
    double checkBalance(OfflinePlayer offlinePlayer, String currency);

    /**
     * Deposits the specified amount of currency into the specified player's account.
     *
     * @param offlinePlayer The player to deposit the currency into
     * @param amount The amount of currency to deposit
     * @param currency The currency ID to use, nullable
     */
    void deposit(OfflinePlayer offlinePlayer, double amount, String currency);

    /**
     * Withdraws the specified amount of currency from the specified player's account.
     *
     * @param offlinePlayer The player to withdraw the currency from
     * @param amount The amount of currency to withdraw
     * @param currency The currency ID to use, nullable
     */
    void withdraw(OfflinePlayer offlinePlayer, double amount, String currency);

}
