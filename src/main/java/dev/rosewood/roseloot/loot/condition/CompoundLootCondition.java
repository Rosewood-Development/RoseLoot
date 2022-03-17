package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.manager.LootConditionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CompoundLootCondition extends LootCondition {

    private final List<LootCondition> conditions;

    public CompoundLootCondition(String compoundTag) {
        super(compoundTag, false);

        this.conditions = new ArrayList<>();

        String[] splitTags = compoundTag.split(Pattern.quote(LootConditionManager.OR_PATTERN));

        LootConditionManager lootConditionManager = RoseLoot.getInstance().getManager(LootConditionManager.class);
        for (String tag : splitTags)
            this.conditions.add(lootConditionManager.parse(tag));
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        return this.conditions.stream().anyMatch(x -> x.check(context));
    }

    @Override
    public boolean parseValues(String[] values) {
        return true;
    }

}
