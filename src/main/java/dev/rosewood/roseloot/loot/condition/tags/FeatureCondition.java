package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.nms.StructureUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.StructureType;

public class FeatureCondition extends LootCondition {

    private List<StructureType> features;

    public FeatureCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Location location = context.getLocation();
        for (StructureType structureType : this.features)
            if (StructureUtils.isWithinStructure(location, structureType))
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
