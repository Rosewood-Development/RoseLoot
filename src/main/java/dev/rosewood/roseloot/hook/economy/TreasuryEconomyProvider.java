package dev.rosewood.roseloot.hook.economy;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.lokka30.treasury.api.common.service.Service;
import me.lokka30.treasury.api.common.service.ServiceRegistry;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class TreasuryEconomyProvider implements EconomyProvider {

    private me.lokka30.treasury.api.economy.EconomyProvider economyProvider;

    public TreasuryEconomyProvider() {
        if (Bukkit.getPluginManager().isPluginEnabled("Treasury"))
            this.economyProvider = ServiceRegistry.INSTANCE.serviceFor(me.lokka30.treasury.api.economy.EconomyProvider.class)
                    .map(Service::get)
                    .orElse(null);
    }

    @Override
    public String formatCurrency(double amount) {
        if (this.economyProvider == null)
            return String.valueOf(amount);
        return this.economyProvider.getPrimaryCurrency().format(new BigDecimal(amount), null, 2);
    }

    @Override
    public double checkBalance(OfflinePlayer offlinePlayer) {
        if (this.economyProvider == null)
            return 0;

        // Our EconomyProvider interface should probably use CompletableFutures, but that will require a large chunk of
        // code changes, so we'll just use a blocking call for now since this isn't actually used yet.
        CompletableFuture<BigDecimal> future = new CompletableFuture<>();

        Currency currency = this.economyProvider.getPrimaryCurrency();
        this.economyProvider.retrievePlayerAccount(offlinePlayer.getUniqueId(), new EconomySubscriber<PlayerAccount>() {
            @Override
            public void succeed(PlayerAccount playerAccount) {
                playerAccount.retrieveBalance(currency, new EconomySubscriber<BigDecimal>() {
                    @Override
                    public void succeed(BigDecimal balance) {
                        future.complete(balance);
                    }

                    @Override
                    public void fail(EconomyException exception) {
                        future.complete(BigDecimal.ZERO);
                    }
                });
            }

            @Override
            public void fail(EconomyException exception) {
                future.complete(BigDecimal.ZERO);
            }
        });

        try {
            return future.get(3, TimeUnit.SECONDS).doubleValue();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        if (this.economyProvider == null)
            return;

        Currency currency = this.economyProvider.getPrimaryCurrency();
        this.economyProvider.retrievePlayerAccount(offlinePlayer.getUniqueId(), new EconomySubscriber<PlayerAccount>() {
            @Override
            public void succeed(PlayerAccount playerAccount) {
                playerAccount.depositBalance(new BigDecimal(amount), EconomyTransactionInitiator.SERVER, currency, new EconomySubscriber<BigDecimal>() {
                    public void succeed(BigDecimal newBalance) { }
                    public void fail(EconomyException exception) { }
                });
            }

            @Override
            public void fail(EconomyException exception) { }
        });
    }

    @Override
    public void withdraw(OfflinePlayer offlinePlayer, double amount) {
        if (this.economyProvider == null)
            return;

        Currency currency = this.economyProvider.getPrimaryCurrency();
        this.economyProvider.retrievePlayerAccount(offlinePlayer.getUniqueId(), new EconomySubscriber<PlayerAccount>() {
            @Override
            public void succeed(PlayerAccount playerAccount) {
                playerAccount.withdrawBalance(new BigDecimal(amount), EconomyTransactionInitiator.SERVER, currency, new EconomySubscriber<BigDecimal>() {
                    public void succeed(BigDecimal newBalance) { }
                    public void fail(EconomyException exception) { }
                });
            }

            @Override
            public void fail(EconomyException exception) { }
        });
    }

}
