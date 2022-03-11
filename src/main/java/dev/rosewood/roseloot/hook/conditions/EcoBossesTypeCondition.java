package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class EcoBossesTypeCondition extends LootCondition {

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

        LivingEntity entity = context.getLootedEntity();
        if (entity == null)
            return false;

        String type = entity.getPersistentDataContainer().get(this.bossKey, PersistentDataType.STRING);
        if (type == null)
            return false;

        return this.types.contains(type);
    }

    @Override
    public boolean parseValues(String[] values) {
        this.types = new ArrayList<>(Arrays.asList(values));
        return !this.types.isEmpty();
    }

}
