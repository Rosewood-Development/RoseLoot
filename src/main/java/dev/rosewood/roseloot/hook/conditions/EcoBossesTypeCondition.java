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

public class EcoBossesTypeCondition extends BaseLootCondition {

    private final NamespacedKey bossKey;
    private List<String> types;

    public EcoBossesTypeCondition(String tag) {
        super(tag);
        Plugin plugin = Bukkit.getPluginManager().getPlugin("EcoBosses");
        this.bossKey = plugin == null ? null : new NamespacedKey(plugin, "boss");
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        if (this.bossKey == null)
            return false;

        return context.get(LootContextParams.LOOTED_ENTITY)
                .map(LivingEntity::getPersistentDataContainer)
                .map(x -> x.get(this.bossKey, PersistentDataType.STRING))
                .filter(this.types::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.types = new ArrayList<>(List.of(values));
        return !this.types.isEmpty();
    }

}
