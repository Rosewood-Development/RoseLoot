package dev.rosewood.roseloot.hook.economy;

import org.bukkit.OfflinePlayer;

public enum EconomyPlugin implements EconomyProvider {

    VAULT(new VaultEconomyProvider()),
    TREASURY(new TreasuryEconomyProvider()),
    PLAYERPOINTS(new PlayerPointsEconomyProvider()),
    TOKENMANAGER(new TokenManagerEconomyProvider());

    private final EconomyProvider economyProvider;

    EconomyPlugin(EconomyProvider economyProvider) {
        this.economyProvider = economyProvider;
    }

    @Override
    public String formatCurrency(double amount) {
        return this.economyProvider.formatCurrency(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        return this.economyProvider.checkBalance(offlinePlayer);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        this.economyProvider.deposit(offlinePlayer, amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        this.economyProvider.withdraw(offlinePlayer, amount);
    }

    public static EconomyPlugin fromString(String name) {
        for (EconomyPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
