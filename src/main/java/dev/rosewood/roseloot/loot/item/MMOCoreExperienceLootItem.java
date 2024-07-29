package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.hook.MMOCoreHook;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class MMOCoreExperienceLootItem implements GroupTriggerableLootItem<MMOCoreExperienceLootItem> {

    private final String profession;
    private final NumberProvider amount;

    protected MMOCoreExperienceLootItem(String profession, NumberProvider amount) {
        this.profession = profession;
        this.amount = amount;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        this.trigger(context, location, List.of());
    }

    @Override
    public void trigger(LootContext context, Location location, List<MMOCoreExperienceLootItem> others) {
        double amount = this.amount.getDouble(context) + others.stream().mapToDouble(x -> x.amount.getDouble(context)).sum();
        context.getLootingPlayer().ifPresent(x -> MMOCoreHook.giveExperience(x, this.profession, amount));
    }

    @Override
    public boolean canTriggerWith(MMOCoreExperienceLootItem other) {
        return this.profession.equals(other.profession);
    }

    public static MMOCoreExperienceLootItem fromSection(ConfigurationSection section) {
        String profession = section.getString("profession");
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        return new MMOCoreExperienceLootItem(profession, amount);
    }

}
