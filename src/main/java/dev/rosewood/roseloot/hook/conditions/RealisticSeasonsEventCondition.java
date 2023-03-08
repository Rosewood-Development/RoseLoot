package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.List;
import java.util.stream.Stream;
import me.casperge.realisticseasons.RealisticSeasons;
import org.bukkit.Location;

public class RealisticSeasonsEventCondition extends BaseLootCondition {

    private List<String> events;

    public RealisticSeasonsEventCondition(String tag) {
        super(tag);
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .map(x -> RealisticSeasons.getInstance().getEventManager().getActiveEvents(x))
                .filter(x -> x.stream().map(String::toLowerCase).anyMatch(this.events::contains))
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.events = Stream.of(values).map(String::toLowerCase).toList();
        return !this.events.isEmpty();
    }

}
