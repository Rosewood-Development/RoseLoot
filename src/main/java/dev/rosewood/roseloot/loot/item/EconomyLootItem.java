package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.hook.economy.EconomyPlugin;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class EconomyLootItem implements GroupTriggerableLootItem<EconomyLootItem> {

    private final EconomyPlugin plugin;
    private final StringProvider currency;
    private final NumberProvider amount;

    protected EconomyLootItem(EconomyPlugin plugin, StringProvider currency, NumberProvider amount) {
        this.plugin = plugin;
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        this.trigger(context, location, List.of());
    }

    @Override
    public void trigger(LootContext context, Location location, List<EconomyLootItem> others) {
        double amount = this.amount.getDouble(context) + others.stream().mapToDouble(x -> x.amount.getDouble(context)).sum();
        String suffix = this.currency == null ? "" : "_" + this.currency;
        context.addPlaceholder("economy_amount", amount);
        if (this.currency != null)
            context.addPlaceholder("economy_amount" + suffix, amount);
        context.addPlaceholder("economy_amount_" + this.plugin.name().toLowerCase() + suffix, amount);
        String currency = this.currency != null ? this.currency.get(context) : null;
        context.getLootingPlayer().ifPresent(x -> this.plugin.deposit(x, amount, currency));
    }

    @Override
    public boolean canTriggerWith(EconomyLootItem other) {
        return this.plugin == other.plugin && Objects.equals(this.currency, other.currency);
    }

    public static EconomyLootItem fromSection(ConfigurationSection section) {
        EconomyPlugin economyPlugin = EconomyPlugin.fromString(section.getString("economy"));
        if (economyPlugin == null)
            return null;

        StringProvider currency = StringProvider.fromSection(section, "currency", null);
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        return new EconomyLootItem(economyPlugin, currency, amount);
    }

}
