package dev.rosewood.roseloot.hook.economy;

import java.util.function.Supplier;
import org.bukkit.OfflinePlayer;

public enum EconomyPlugin implements EconomyProvider {

    VAULT(VaultEconomyProvider::new),
    TREASURY(TreasuryEconomyProvider::new),
    PLAYERPOINTS(PlayerPointsEconomyProvider::new),
    TOKENMANAGER(TokenManagerEconomyProvider::new);

    private final Supplier<EconomyProvider> lazyLoader;
    private EconomyProvider economyProvider;

    EconomyPlugin(Supplier<EconomyProvider> lazyLoader) {
        this.lazyLoader = lazyLoader;
    }

    @Override
    public String formatCurrency(double amount) {
        return this.load().formatCurrency(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        return this.load().checkBalance(offlinePlayer);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        this.load().deposit(offlinePlayer, amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        this.load().withdraw(offlinePlayer, amount);
    }

    private EconomyProvider load() {
        if (this.economyProvider == null)
            this.economyProvider = this.lazyLoader.get();
        return this.economyProvider;
    }

    public static EconomyPlugin fromString(String name) {
        for (EconomyPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
