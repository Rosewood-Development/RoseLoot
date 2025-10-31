package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
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
    public void trigger(LootContext context, Location location, List<MMOCoreExperienceLootItem> others) {
        context.getLootingPlayer().ifPresent(player -> {
            double amount = this.amount.getDouble(context) + others.stream().mapToDouble(x -> x.amount.getDouble(context)).sum();
            PlayerData playerData = PlayerData.get(player);
            if (this.profession == null) {
                playerData.giveExperience(amount, EXPSource.COMMAND);
                return;
            }

            Profession type = MMOCore.plugin.professionManager.get(this.profession);
            if (type != null) {
                type.giveExperience(playerData, amount, null, EXPSource.COMMAND);
            } else {
                RoseLoot.getInstance().getLogger().warning("Invalid MMOCore Profession: " + this.profession);
            }
        });
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
