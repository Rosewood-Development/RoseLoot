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
import org.bukkit.StructureType;

@SuppressWarnings("deprecation")
public class LegacyFeatureCondition extends BaseLootCondition {

    private List<StructureType> features;

    public LegacyFeatureCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Location> origin = context.get(LootContextParams.ORIGIN);
        if (origin.isEmpty())
            return false;

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        for (StructureType structureType : this.features)
            if (nmsHandler.isWithinStructure(origin.get(), structureType.getKey()))
                return true;

        return false;
    }

    @Override
    public boolean parseValues(String[] values) {
        this.features = new ArrayList<>();

        for (String value : values) {
            try {
                StructureType structureType = StructureType.getStructureTypes().get(value.toLowerCase());
                if (structureType != null)
                    this.features.add(structureType);
            } catch (Exception ignored) { }
        }

        return !this.features.isEmpty();
    }

}
