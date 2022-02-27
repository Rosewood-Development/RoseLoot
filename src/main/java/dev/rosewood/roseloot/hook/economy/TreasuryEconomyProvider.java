package dev.rosewood.roseloot.hook.economy;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.lokka30.treasury.api.common.service.Service;
import me.lokka30.treasury.api.common.service.ServiceRegistry;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class TreasuryEconomyProvider implements EconomyProvider {

    private final boolean enabled;
    private me.lokka30.treasury.api.economy.EconomyProvider economyProvider;

    public TreasuryEconomyProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Treasury");
        if (this.enabled)
            this.economyProvider = ServiceRegistry.INSTANCE.serviceFor(me.lokka30.treasury.api.economy.EconomyProvider.class)
                    .map(Service::get)
                    .orElse(null);
    }

    @Override
    public String formatCurrency(double amount) {
        if (!this.enabled)
            return String.valueOf(amount);
        return this.economyProvider.getPrimaryCurrency().format(new BigDecimal(amount), null, 2);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (!this.enabled)
            return 0;

        // Our EconomyProvider interface should probably use CompletableFutures, but that will require a large chunk of
        // code changes, so we'll just use a blocking call for now since this isn't actually used yet.
        try {
            return EconomySubscriber.<PlayerAccount>asFuture(subscriber -> this.economyProvider.retrievePlayerAccount(offlinePlayer.getUniqueId(), subscriber))
                    .thenCompose(playerAccount -> EconomySubscriber.<BigDecimal>asFuture(subscriber -> playerAccount.retrieveBalance(this.economyProvider.getPrimaryCurrency(), subscriber)))
                    .get(3, TimeUnit.SECONDS).doubleValue();
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (!this.enabled)
            return;

        EconomySubscriber.<PlayerAccount>asFuture(subscriber -> this.economyProvider.retrievePlayerAccount(offlinePlayer.getUniqueId(), subscriber))
                .thenCompose(playerAccount -> EconomySubscriber.<BigDecimal>asFuture(subscriber -> playerAccount.depositBalance(new BigDecimal(amount), EconomyTransactionInitiator.SERVER, this.economyProvider.getPrimaryCurrency(), subscriber)))
                .whenComplete((x, y) -> { /* Don't care about the result */ });
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (!this.enabled)
            return;

        EconomySubscriber.<PlayerAccount>asFuture(subscriber -> this.economyProvider.retrievePlayerAccount(offlinePlayer.getUniqueId(), subscriber))
                .thenCompose(playerAccount -> EconomySubscriber.<BigDecimal>asFuture(subscriber -> playerAccount.withdrawBalance(new BigDecimal(amount), EconomyTransactionInitiator.SERVER, this.economyProvider.getPrimaryCurrency(), subscriber)))
                .whenComplete((x, y) -> { /* Don't care about the result */ });
    }

}
