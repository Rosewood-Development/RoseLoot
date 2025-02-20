package dev.rosewood.roseloot.hook.economy;

import dev.rosewood.roseloot.util.Lazy;
import java.util.function.Supplier;
import org.bukkit.OfflinePlayer;

public enum EconomyPlugin implements EconomyProvider {

    VAULT(VaultEconomyProvider::new),
    TREASURY(TreasuryEconomyProvider::new),
    PLAYERPOINTS(PlayerPointsEconomyProvider::new),
    TOKENMANAGER(TokenManagerEconomyProvider::new),
    COINSENGINE(CoinsEngineEconomyProvider::new);

    private final Lazy<EconomyProvider> economyProvider;

    EconomyPlugin(Supplier<EconomyProvider> lazyLoader) {
        this.economyProvider = new Lazy<>(lazyLoader);
    }

    @Override
    public String formatCurrency(double amount, String currency) {
        return this.economyProvider.get().formatCurrency(amount, currency);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer, String currency) {
        return this.economyProvider.get().checkBalance(offlinePlayer, currency);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount, String currency) {
        this.economyProvider.get().deposit(offlinePlayer, amount, currency);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount, String currency) {
        this.economyProvider.get().withdraw(offlinePlayer, amount, currency);
    }

    public static EconomyPlugin fromString(String name) {
        for (EconomyPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
