package dev.rosewood.roseloot.loot.item;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class AuraSkillsExperienceLootItem implements GroupTriggerableLootItem<AuraSkillsExperienceLootItem> {

    private final String skill;
    private final NumberProvider amount;
    private final boolean raw;

    protected AuraSkillsExperienceLootItem(String skill, NumberProvider amount, boolean raw) {
        this.skill = skill;
        this.amount = amount;
        this.raw = raw;
    }

    @Override
    public void trigger(LootContext context, Location location, List<AuraSkillsExperienceLootItem> others) {
        context.getLootingPlayer().ifPresent(player -> {
            AuraSkillsApi api = AuraSkillsApi.get();
            double amount = this.amount.getDouble(context) + others.stream().mapToDouble(x -> x.amount.getDouble(context)).sum();
            SkillsUser user = api.getUser(player.getUniqueId());
            Skill skill = api.getGlobalRegistry().getSkill(NamespacedId.fromDefault(this.skill));

            if (skill == null) {
                RoseLoot.getInstance().getLogger().warning("Invalid AuraSkills skill: " + this.skill);
                return;
            }

            if (this.raw) {
                user.addSkillXp(skill, amount);
            } else {
                user.addSkillXpRaw(skill, amount);
            }
        });
    }

    @Override
    public boolean canTriggerWith(AuraSkillsExperienceLootItem other) {
        return this.skill.equals(other.skill) && this.raw == other.raw;
    }

    public static AuraSkillsExperienceLootItem fromSection(ConfigurationSection section) {
        String skill = section.getString("skill");
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        boolean giveNaturally = section.getBoolean("raw", true);
        return new AuraSkillsExperienceLootItem(skill, amount, giveNaturally);
    }

}
