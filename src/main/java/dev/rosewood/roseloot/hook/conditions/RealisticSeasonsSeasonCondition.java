package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.List;
import java.util.stream.Stream;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.season.Season;
import org.bukkit.Location;

public class RealisticSeasonsSeasonCondition extends BaseLootCondition {

    private List<String> seasons;

    public RealisticSeasonsSeasonCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        SeasonsAPI api = SeasonsAPI.getInstance();
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getWorld)
                .map(api::getSeason)
                .map(Season::getConfigName)
                .map(String::toLowerCase)
                .filter(this.seasons::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.seasons = Stream.of(values).map(String::toLowerCase).toList();
        return !this.seasons.isEmpty();
    }

}
