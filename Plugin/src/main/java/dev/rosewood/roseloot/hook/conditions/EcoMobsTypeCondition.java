package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class EcoMobsTypeCondition extends BaseLootCondition {

    private final NamespacedKey mobKey;
    private List<String> types;

    public EcoMobsTypeCondition(String tag) {
        super(tag);
        Plugin plugin = Bukkit.getPluginManager().getPlugin("EcoMobs");
        this.mobKey = plugin == null ? null : new NamespacedKey(plugin, "mob");
    }

    @Override
    public boolean check(LootContext context) {
        if (this.mobKey == null)
            return false;

        return context.get(LootContextParams.LOOTED_ENTITY)
                .map(LivingEntity::getPersistentDataContainer)
                .map(x -> x.get(this.mobKey, PersistentDataType.STRING))
                .filter(this.types::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.types = new ArrayList<>(List.of(values));
        return !this.types.isEmpty();
    }

}
