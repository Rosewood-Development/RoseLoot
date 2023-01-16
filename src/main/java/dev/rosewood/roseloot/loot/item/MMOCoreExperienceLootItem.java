package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.hook.MMOCoreHook;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class MMOCoreExperienceLootItem implements TriggerableLootItem {

    private final String profession;
    private final NumberProvider amount;

    public MMOCoreExperienceLootItem(String profession, NumberProvider amount) {
        this.profession = profession;
        this.amount = amount;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        context.getLootingPlayer().ifPresent(x -> MMOCoreHook.giveExperience(x, this.profession, this.amount.getDouble(context)));
    }

    public static MMOCoreExperienceLootItem fromSection(ConfigurationSection section) {
        String profession = section.getString("profession");
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        return new MMOCoreExperienceLootItem(profession, amount);
    }

}
