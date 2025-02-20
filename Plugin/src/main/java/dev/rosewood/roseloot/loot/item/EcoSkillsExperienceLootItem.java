package dev.rosewood.roseloot.loot.item;

import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.skills.Skills;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class EcoSkillsExperienceLootItem implements GroupTriggerableLootItem<EcoSkillsExperienceLootItem> {

    private final String skill;
    private final NumberProvider amount;
    private final boolean giveNaturally;

    protected EcoSkillsExperienceLootItem(String skill, NumberProvider amount, boolean giveNaturally) {
        this.skill = skill;
        this.amount = amount;
        this.giveNaturally = giveNaturally;
    }

    @Override
    public void trigger(LootContext context, Location location, List<EcoSkillsExperienceLootItem> others) {
        double amount = this.amount.getDouble(context) + others.stream().mapToDouble(x -> x.amount.getDouble(context)).sum();
        context.getLootingPlayer().ifPresent(player -> {
            Skill skill = Skills.INSTANCE.get(this.skill);
            if (skill == null) {
                RoseLoot.getInstance().getLogger().warning("Invalid EcoSkills skill: " + this.skill);
                return;
            }

            if (this.giveNaturally) {
                EcoSkillsAPI.gainSkillXP(player, skill, amount);
            } else {
                EcoSkillsAPI.giveSkillXP(player, skill, amount);
            }
        });
    }

    @Override
    public boolean canTriggerWith(EcoSkillsExperienceLootItem other) {
        return this.skill.equals(other.skill) && this.giveNaturally == other.giveNaturally;
    }

    public static EcoSkillsExperienceLootItem fromSection(ConfigurationSection section) {
        String profession = section.getString("skill");
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        boolean giveNaturally = section.getBoolean("natural", true);
        return new EcoSkillsExperienceLootItem(profession, amount, giveNaturally);
    }

}
