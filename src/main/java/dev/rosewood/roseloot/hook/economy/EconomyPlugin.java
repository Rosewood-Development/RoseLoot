package dev.rosewood.roseloot.hook.economy;

import dev.rosewood.roseloot.util.Lazy;
import java.util.function.Supplier;
import org.bukkit.OfflinePlayer;

public enum EconomyPlugin implements EconomyProvider {

    VAULT(VaultEconomyProvider::new),
    TREASURY(TreasuryEconomyProvider::new),
    PLAYERPOINTS(PlayerPointsEconomyProvider::new),
    TOKENMANAGER(TokenManagerEconomyProvider::new);

    private final Lazy<EconomyProvider> economyProvider;

    EconomyPlugin(Supplier<EconomyProvider> lazyLoader) {
        this.economyProvider = new Lazy<>(lazyLoader);
    }

    @Override
    public String formatCurrency(double amount) {
        return this.economyProvider.get().formatCurrency(amount);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        return this.economyProvider.get().checkBalance(offlinePlayer);
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        this.economyProvider.get().deposit(offlinePlayer, amount);
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        this.economyProvider.get().withdraw(offlinePlayer, amount);
    }

    public static EconomyPlugin fromString(String name) {
        for (EconomyPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
