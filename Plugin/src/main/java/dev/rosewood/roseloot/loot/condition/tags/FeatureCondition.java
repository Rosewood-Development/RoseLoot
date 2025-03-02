package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.nms.NMSAdapter;
import dev.rosewood.roseloot.nms.NMSHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.generator.structure.Structure;

public class FeatureCondition extends BaseLootCondition {

    private List<NamespacedKey> features;

    public FeatureCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Location> origin = context.get(LootContextParams.ORIGIN);
        if (origin.isEmpty())
            return false;

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        for (NamespacedKey structure : this.features)
            if (nmsHandler.isWithinStructure(origin.get(), structure))
                return true;

        return false;
    }

    @Override
    public boolean parseValues(String[] values) {
        this.features = new ArrayList<>();

        for (String value : values) {
            try {
                NamespacedKey namespacedKey = NamespacedKey.fromString(value);
                if (namespacedKey == null)
                    continue;

                Structure structure = Registry.STRUCTURE.get(namespacedKey);
                if (structure != null)
                    this.features.add(namespacedKey);
            } catch (Exception ignored) { }
        }

        return !this.features.isEmpty();
    }

}
