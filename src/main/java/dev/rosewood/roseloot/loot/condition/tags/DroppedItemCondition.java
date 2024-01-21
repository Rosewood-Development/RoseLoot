package dev.rosewood.roseloot.loot.condition.tags;

@Deprecated(forRemoval = true)
public class DroppedItemCondition extends InputItemCondition {

    public DroppedItemCondition(String tag) {
        super(tag);
    }

    @Override
    protected String getDeprecationReplacement() {
        return "input-item";
    }

}
