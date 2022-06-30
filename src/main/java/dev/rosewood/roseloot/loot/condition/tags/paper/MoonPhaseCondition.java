package dev.rosewood.roseloot.loot.condition.tags.paper;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import io.papermc.paper.world.MoonPhase;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class MoonPhaseCondition extends LootCondition {

    private List<MoonPhase> moonPhases;

    public MoonPhaseCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .filter(x -> this.moonPhases.contains(x.getMoonPhase()))
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.moonPhases = new ArrayList<>();

        for (String value : values) {
            try {
                this.moonPhases.add(MoonPhase.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.moonPhases.isEmpty();
    }

}
